package com.keplersegg.myself.helper

import android.content.Context
import com.keplersegg.myself.MySelfApplication
import com.keplersegg.myself.Room.Entity.Task
import org.jetbrains.anko.doAsync

object CallDurationHelper {

    private var flag = false
    private var start: Long = 0

    fun startCall() {

        flag = true
        start = System.currentTimeMillis()
    }

    fun endCall(context: Context) {

        if (flag) {

            flag = false
            val duration = System.currentTimeMillis() - start
            start = 0
            updateCallDuration(context, duration)
        }
    }

    private fun updateCallDuration(context: Context, duration: Long) {

        val today = Utils.getToday()
        val app = context.applicationContext as MySelfApplication

        var totalDuration = app.dataStore.getTotalCallDuration(today)
        totalDuration += duration
        app.dataStore.setTotalCallDuration(today, totalDuration)

        var minutes = (totalDuration / 60000).toInt()
        if (minutes == 0 && duration > 0) minutes = 1

        doAsync {

            val tasks = TaskUpdater(context).AppDB()
                    .taskDao().getAll(1).filter { t -> t.AutomationType == AutoTaskType.CallDuration.typeId }

            for (task: Task in tasks) {

                TaskUpdater(context).updateEntry(task.Id, today, minutes)
            }
        }
    }
}