package com.keplersegg.myself.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.keplersegg.myself.helper.CallDurationHelper

class CallDurationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val action = intent.action

        if (action == "android.intent.action.PHONE_STATE") {
            if (intent.getStringExtra(TelephonyManager.EXTRA_STATE) == TelephonyManager.EXTRA_STATE_OFFHOOK) {

                CallDurationHelper.startCall()
            }

            if (intent.getStringExtra(TelephonyManager.EXTRA_STATE) == TelephonyManager.EXTRA_STATE_IDLE) {

                CallDurationHelper.endCall(context)
            }
        }
    }


}