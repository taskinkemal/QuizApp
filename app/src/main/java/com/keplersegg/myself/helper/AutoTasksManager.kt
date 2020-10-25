package com.keplersegg.myself.helper

import android.app.usage.UsageStatsManager
import android.content.Context
import java.util.*
import android.provider.CallLog
import com.keplersegg.myself.Room.AppDatabase
import com.keplersegg.myself.Room.Entity.Entry
import com.keplersegg.myself.Room.Entity.Task
import org.jetbrains.anko.doAsync
import android.net.wifi.WifiManager
import android.net.ConnectivityManager

open class AutoTasksManager {

    fun Run(context: Context, runnable: Runnable) {

        val networkId = getCurrentNetworkId(context)

        doAsync {

            val tasks = getAppDB(context)
                    .taskDao().getAll(1).filter { t -> t.AutomationType != null }

            for (task: Task in tasks) {

                when (AutoTaskType.valueOf(task.AutomationType!!)) {

                    AutoTaskType.CallDuration -> { getCallDurations(context, task.Id) }
                    AutoTaskType.AppUsage -> { getAppUsage(context, task.Id, Utils.toInt(task.AutomationVar)) }
                    AutoTaskType.WentTo -> {

                        if (networkId != null && Utils.toInt(task.AutomationVar) == networkId) {

                            updateEntry(context, task.Id, Utils.getToday(), 1)
                        }
                    }
                }
            }

            runnable.run()
        }
    }

    private fun getAppUsage(context: Context, taskId: Int, id: Int?) {

        if (id == null)
        {
            return
        }

        for (i: Int in -5 .. 0) {

            val mUsageStatsManager: UsageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

            val fromDate = Utils.getDayBack(i)
            val toDate = Utils.getDayBack(i + 1)

            val stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST,
                    fromDate, toDate)

            val today = Utils.getToday()
            val pkgStats = stats.firstOrNull { s -> s.packageName.hashCode() == id }

            if (pkgStats != null) {

                val totalTime = pkgStats.totalTimeInForeground

                var minutes = (totalTime / 60000).toInt()

                if (minutes == 0 && totalTime > 0) minutes = 1

                updateEntry(context, taskId, today + i, minutes)

            }
        }
    }

    private fun updateEntry(context: Context, taskId: Int, day: Int, duration: Int) {

        var entry = getAppDB(context).entryDao()[day, taskId]

        if (entry == null) {

            entry = Entry()
            entry.TaskId = taskId
            entry.Day = day
            entry.Value = duration
            entry.ModificationDate = Date(System.currentTimeMillis())
            getAppDB(context).entryDao().insert(entry)
        }
        else {
            entry.Value = duration
            entry.ModificationDate = Date(System.currentTimeMillis())
            getAppDB(context).entryDao().update(entry)
        }
    }

    private fun getAppDB(context: Context) : AppDatabase {
        return AppDatabase
                .getAppDatabase(context)!!
    }

    private fun getCallDurations(context: Context, taskId: Int) {

        for (i: Int in -5 .. 0) {

            val duration = getCalldetails(context, i)
            val today = Utils.getToday()

            var minutes = duration / 60

            if (minutes == 0 && duration > 0) minutes = 1

            updateEntry(context, taskId, today + i, minutes)

            //if (HttpClient.hasInternetAccess(application!!)) {
            //    uploadEntry(item.entry!!)
            //}
        }
    }

    private fun getCalldetails(context: Context, daysBack: Int) : Int {
        var totalSeconds = 0

        val strOrder = CallLog.Calls.DATE + " DESC"

        val fromDate = Utils.getDayBack(daysBack)
        val toDate = Utils.getDayBack(daysBack + 1)

        val whereValue = arrayOf(fromDate.toString(), toDate.toString())

        val managedCursor = context.contentResolver.query(CallLog.Calls.CONTENT_URI, null, CallLog.Calls.DATE + " BETWEEN ? AND ?", whereValue, strOrder)

        if (managedCursor != null) {

            val duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION)

            while (managedCursor.moveToNext()) {

                totalSeconds += managedCursor.getInt(duration)

            }

            managedCursor.close()
        }

        return totalSeconds
    }

    private fun getCurrentNetworkId(context: Context): Int? {
        var networkId: Int? = null
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo ?: return null

        if (networkInfo.isConnected) {
            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val connectionInfo = wifiManager.connectionInfo
            if (connectionInfo != null) {
                networkId = connectionInfo.networkId
            }
        }

        return networkId
    }
}