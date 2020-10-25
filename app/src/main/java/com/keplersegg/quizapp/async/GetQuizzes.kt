package com.keplersegg.quizapp.async

import android.Manifest
import android.os.AsyncTask
import com.keplersegg.quizapp.helper.ServiceMethods
import com.keplersegg.quizapp.interfaces.IGetQuizzesHost

import com.keplersegg.quizapp.Room.Entity.Task
import com.keplersegg.quizapp.helper.AutoTaskType
import com.keplersegg.quizapp.helper.PermissionsHelper
import com.keplersegg.quizapp.models.UploadEntryResponse


open class SyncTasks(private var activity: IGetQuizzesHost) : AsyncTask<Void, Void, List<AutoTaskType>>() {

    override fun doInBackground(vararg params: Void?): List<AutoTaskType> {

        val missingPermissions = ArrayList<AutoTaskType>()
        val list = ServiceMethods.getTasksFromService(activity)

        if (list != null) {

            // successfully connected to the server and retrieved the list.

            for (i in 0 until list.size) {

                upsertTask(list[i])

                if (PermissionChecker.isPermissionMissing(activity, list[i].AutomationType)) missingPermissions.add(AutoTaskType.valueOf(list[i].AutomationType!!)!!)
            }

            val listLocal = activity.AppDB().taskDao().all

            for (i in 0 until listLocal.size) {

                val foundTask = list.firstOrNull { t -> t.Id == listLocal[i].Id }
                if (foundTask == null) {

                    if (listLocal[i].Id < 0) {

                        // newly added task without internet connection

                        val taskId = ServiceMethods.uploadTask(activity, listLocal[i])
                        if (taskId > 0) /* check if uploaded successfully */ {
                            activity.AppDB().taskDao().updateId(taskId, listLocal[i].Id)
                            listLocal[i].Id = taskId
                        }
                    }
                    else {

                        // shouldn't come here
                        ServiceMethods.uploadTask(activity, listLocal[i])
                    }

                    if (PermissionChecker.isPermissionMissing(activity, listLocal[i].AutomationType)) missingPermissions.add(AutoTaskType.valueOf(listLocal[i].AutomationType!!)!!)
                }
            }

            val entries = ServiceMethods.getEntriesFromService(activity)

            if (entries != null) {

                val entriesLocal = activity.AppDB().entryDao().all

                for (i in 0 until entriesLocal.size) {

                    val entry = entries.firstOrNull { t ->
                        t.Day == entriesLocal[i].Day &&
                                t.TaskId == entriesLocal[i].TaskId}

                    if (entry != null) {

                        if (entry.ModificationDate > entriesLocal[i].ModificationDate) {

                            entriesLocal[i].Value = entry.Value
                            activity.AppDB().entryDao().update(entriesLocal[i])
                        }
                        else if (entry.ModificationDate < entriesLocal[i].ModificationDate) {

                            val uploadEntryResponse = ServiceMethods.uploadEntry(activity, entriesLocal[i])
                            upsertBadge(uploadEntryResponse)
                        }
                    }
                    else {

                        val uploadEntryResponse = ServiceMethods.uploadEntry(activity, entriesLocal[i])
                        upsertBadge(uploadEntryResponse)
                    }
                }

                for (i in 0 until entries.size) {

                    if (!entriesLocal.any { t ->
                                t.Day == entries[i].Day &&
                                t.TaskId == entries[i].TaskId}) {

                        activity.AppDB().entryDao().insert(entries[i])
                    }
                }
            }
        }
        else {

            // can be disconnected or not logged in.

            val listLocal = activity.AppDB().taskDao().all

            if (listLocal.isEmpty()) {

                val newItem1 = Task.CreateItem(-2, "Exercise", 0, "", null,null)
                val newItem2 = Task.CreateItem(-3, "Study", 0, "", null,null)
                val newItem3 = Task.CreateItem(-4, "Coffee", 1, "cup(s)", null,null)

                activity.AppDB().taskDao().insert(newItem1)
                activity.AppDB().taskDao().insert(newItem2)
                activity.AppDB().taskDao().insert(newItem3)
            }
        }

        return missingPermissions.distinct()
    }

    override fun onPostExecute(result: List<AutoTaskType>) {

        activity.onSyncTasksSuccess(result)
    }

    private fun upsertTask(task: Task) {

        val rowsAffected = activity.AppDB().taskDao().update(task)
        if (rowsAffected == 0) {
            activity.AppDB().taskDao().insert(task)
        }
    }

    fun upsertBadge(response: UploadEntryResponse?) {

        if (response != null) {

            SyncBadges(activity).upsertBadge(response.Score, response.NewBadges)
        }
    }

    object PermissionChecker {

        fun isPermissionMissing(activity: IGetQuizzesHost, autoTaskType: Int?) : Boolean {

            if (autoTaskType == null || autoTaskType < 1 || autoTaskType > 3) return false

            var result = false

            when (AutoTaskType.valueOf(autoTaskType)!!) {

                AutoTaskType.CallDuration -> {

                    result = PermissionsHelper.shouldRequestForPermission(activity.GetMasterActivity(), Manifest.permission.READ_PHONE_STATE)
                }
                AutoTaskType.AppUsage -> {

                    result = !PermissionsHelper.checkForUsageSettingsPermission(activity.GetMasterActivity())
                    // || PermissionsHelper.shouldRequestForPermission(activity.GetMasterActivity(), Manifest.permission.PACKAGE_USAGE_STATS)
                }
                AutoTaskType.WentTo -> { }
            }

            return result
        }
    }
}