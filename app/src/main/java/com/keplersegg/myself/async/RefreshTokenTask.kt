package com.keplersegg.myself.async

import android.os.AsyncTask
import com.keplersegg.myself.helper.HttpClient
import com.keplersegg.myself.interfaces.IRefreshTokenHost

import org.json.JSONObject


open class RefreshTokenTask(private var activity: IRefreshTokenHost) : AsyncTask<String, Void, JSONObject?>() {

    override fun doInBackground(vararg params: String?): JSONObject? {

        val token = if (params.isNotEmpty()) params[0] else null
        val deviceID = if (params.size > 1) params[1] else null

        val jsonParams = JSONObject()
        try {

            jsonParams.put("AccessToken", token)
            jsonParams.put("DeviceID", deviceID)
        } catch (exc: Exception) {

            activity.logException(exc, "RefreshTokenTask.doInBackground")
        }

        return HttpClient.send(activity, "token/refresh", "post", jsonParams)
    }

    override fun onPostExecute(result: JSONObject?) {

        try {

            if (result == null) {

                activity.onRefreshError("General error")
            }
            else if (result.has("Code") && result.has("Message")) {

                activity.onRefreshError(result.getString("Message"))
            } else {

                activity.setAccessToken(result.getString("Token"))
                activity.onRefreshSuccess()
            }
        } catch (exc: Exception) {

            activity.logException(exc, "RefreshTokenTask.onPostExecute")
        }

    }
}