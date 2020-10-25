package com.keplersegg.myself.helper

import android.content.Context
import android.net.ConnectivityManager
import com.crashlytics.android.Crashlytics
import com.keplersegg.myself.MySelfApplication
import com.keplersegg.myself.Room.AppDatabase
import com.keplersegg.myself.Room.Entity.Entry
import com.keplersegg.myself.async.SyncBadges
import com.keplersegg.myself.interfaces.IHttpProvider
import com.keplersegg.myself.interfaces.ISyncHost
import java.lang.Exception
import java.util.*

class TaskUpdater(context: Context) : IHttpProvider, ISyncHost {

    private var app = context.applicationContext as MySelfApplication

    fun updateEntry(taskId: Int, day: Int, value: Int) {

        var entry = AppDB().entryDao()[day, taskId]

        if (entry == null) {

            entry = Entry()
            entry.TaskId = taskId
            entry.Day = day
            entry.Value = value
            entry.ModificationDate = Date(System.currentTimeMillis())
            AppDB().entryDao().insert(entry)
        }
        else {
            entry.Value = value
            entry.ModificationDate = Date(System.currentTimeMillis())
            AppDB().entryDao().update(entry)
        }

        if (HttpClient.hasInternetAccess(this)) {

            val response = ServiceMethods.uploadEntry(this, entry)

            if (response != null) {

                SyncBadges(this).upsertBadge(response.Score, response.NewBadges)
            }
        }
    }

    override fun getAccessToken(): String? {
        return app.dataStore.getAccessToken()
    }

    override fun getDeviceId(): String? {
        return app.dataStore.getRegisterID()
    }

    override fun getConnectivityManager(): ConnectivityManager {
        return app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    override fun showErrorMessage(message: String) { }

    override fun logException(exception: Exception, message: String) {
        Crashlytics.logException(exception)
    }

    override fun AppDB() : AppDatabase {
        return AppDatabase
                .getAppDatabase(app)!!
    }

    override fun GetApplication() : MySelfApplication {
        return app
    }
}