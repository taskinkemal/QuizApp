package com.keplersegg.myself.async

import android.os.AsyncTask
import com.keplersegg.myself.Room.Entity.Goal
import com.keplersegg.myself.helper.ServiceMethods
import com.keplersegg.myself.interfaces.ISyncGoalsHost


open class SyncGoals(private var activity: ISyncGoalsHost) : AsyncTask<Void, Void, List<Goal>>() {

    override fun doInBackground(vararg params: Void?): List<Goal> {

        val list = ServiceMethods.getGoalsFromService(activity)

        if (list != null) {

            // successfully connected to the server and retrieved the list.

            for (i in 0 until list.size) {

                upsertGoal(list[i])
            }

            val listLocal = activity.AppDB().goalDao().all

            for (i in 0 until listLocal.size) {

                val foundGoal = list.firstOrNull { t -> t.Id == listLocal[i].Id }
                if (foundGoal == null) {

                    if (listLocal[i].Id < 0) {

                        // newly added goal without internet connection

                        val response = ServiceMethods.uploadGoal(activity, listLocal[i])
                        if (response != null) /* check if uploaded successfully */ {
                            activity.AppDB().goalDao().updateId(response.GoalId, listLocal[i].Id)
                            listLocal[i].Id = response.GoalId

                            SyncBadges(activity).upsertBadge(response.Score, response.NewBadges)
                        }
                    }
                    else {

                        // shouldn't come here
                        ServiceMethods.uploadGoal(activity, listLocal[i])
                    }
                }
            }

            return listLocal
        }
        else {

            return activity.AppDB().goalDao().all
        }
    }

    override fun onPostExecute(result: List<Goal>) {

        activity.onSyncGoalsSuccess(result)
    }

    private fun upsertGoal(goal: Goal) {

        val rowsAffected = activity.AppDB().goalDao().update(goal)
        if (rowsAffected == 0) {
            activity.AppDB().goalDao().insert(goal)
        }
    }
}