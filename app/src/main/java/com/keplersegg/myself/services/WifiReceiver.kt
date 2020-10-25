package com.keplersegg.myself.services

import android.net.wifi.WifiManager
import android.net.NetworkInfo
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import com.keplersegg.myself.helper.AutoTaskType
import com.keplersegg.myself.helper.Utils
import com.keplersegg.myself.Room.Entity.Task
import com.keplersegg.myself.helper.TaskUpdater
import org.jetbrains.anko.doAsync


class WifiReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val action = intent.action

        if (action == WifiManager.NETWORK_STATE_CHANGED_ACTION) {

            val info = intent.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO)
            if (info != null && info.isConnected) {
                // Do your work.

                // e.g. To check the Network Name or other info:
                val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val wifiInfo = wifiManager.connectionInfo
                val networkId = wifiInfo.ssid

                doAsync {

                    val tasks = TaskUpdater(context).AppDB()
                            .taskDao().getAll(1)
                            .filter { t -> t.AutomationType == AutoTaskType.WentTo.typeId && t.AutomationVar == networkId }

                    for (task: Task in tasks) {

                        TaskUpdater(context).updateEntry(task.Id, Utils.getToday(), 1)
                    }
                }
            }
        }
    }
}