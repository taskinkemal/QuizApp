package com.keplersegg.myself

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.google.android.gms.ads.MobileAds

import com.keplersegg.myself.helper.DataStorage
import com.keplersegg.myself.models.User
import io.fabric.sdk.android.Fabric

class MySelfApplication : Application() {

    var user: User? = null
    lateinit var dataStore: DataStorage
    private lateinit var androidDefaultUEH: Thread.UncaughtExceptionHandler
    private var handler: Thread.UncaughtExceptionHandler = Thread.UncaughtExceptionHandler {
        _, e ->
        Crashlytics.logException(e)
    }

    override fun onCreate() {

        super.onCreate()

        this.dataStore = DataStorage(this)

        Fabric.with(this, Crashlytics())
        androidDefaultUEH = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(handler)

        MobileAds.initialize(this, "ca-app-pub-4857110393196284~6736902199")
    }

    fun clearSession() {

        user = null
        dataStore.setAccessToken(null)
        dataStore.setGoogleToken(null)
        dataStore.setFacebookToken(null)
    }
}