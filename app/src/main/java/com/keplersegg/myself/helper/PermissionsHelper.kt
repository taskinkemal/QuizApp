package com.keplersegg.myself.helper

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Process
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.keplersegg.myself.R
import com.keplersegg.myself.activities.MasterActivity
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread



object PermissionsHelper {

    fun CheckPermission(activity: MasterActivity, permission: String, requestCode: Int, onSuccess: Runnable) {

        if (ContextCompat.checkSelfPermission(activity, permission)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, Array(1) { permission }, requestCode)
        }
        else {
            onSuccess.run()
        }
    }

    fun CheckActionUsageSettingsPermission(activity: MasterActivity) : Boolean {

        if (!checkForUsageSettingsPermission(activity))
        {
            activity.showErrorMessage(activity.getString(R.string.permission_required_apps))
            doAsync {
                uiThread {
                    val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                    activity.startActivity(intent)
                }
            }
        }

        if (!checkForUsageSettingsPermission(activity)) {
            activity.showErrorMessage(activity.getString(R.string.permission_error_apps))
            return false
        }

        return true
    }

    fun shouldRequestForPermission(activity: MasterActivity, permission: String): Boolean {

        if (ContextCompat.checkSelfPermission(activity, permission)
                != PackageManager.PERMISSION_GRANTED) {
            return !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
        }

        return false
    }

    fun checkForUsageSettingsPermission(context: Context): Boolean {

        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
        return mode == AppOpsManager.MODE_ALLOWED
    }
}