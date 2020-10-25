package com.keplersegg.myself.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.crashlytics.android.Crashlytics

import com.keplersegg.myself.activities.MainActivity
import com.keplersegg.myself.interfaces.IErrorMessage
import com.keplersegg.myself.Room.AppDatabase
import java.lang.Exception

abstract class MasterFragment : Fragment(), IErrorMessage {

    protected lateinit var activity: MainActivity
    protected var rootView: View? = null
    protected var layout: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (rootView == null) {

            rootView = inflater.inflate(layout, container, false)
            activity = rootView!!.context as MainActivity

            onCreateViewInternal()
        }

        return rootView
    }

    override fun onResume() {
        super.onResume()

        logAnalyticsPageVisit()
    }

    open fun onCreateViewInternal() { }

    fun SetTitle(id: Int) {

        activity.setTitle(id)
    }

    fun getConnectivityManager(): ConnectivityManager {
        return activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    fun getAppDB(): AppDatabase { return activity.AppDB() }

    fun getDeviceId(): String? { return activity.getDeviceId() }

    fun getAccessToken(): String? { return activity.app.dataStore.getAccessToken() }

    override fun logException(exception: Exception, message: String) {

        Crashlytics.logException(exception)
        activity.showErrorMessage(message)
    }

    override fun showErrorMessage(message: String) {

        activity.showErrorMessage(message)
    }


    private fun logAnalyticsPageVisit() {

        activity.firebaseAnalytics.setCurrentScreen(activity, javaClass.simpleName, javaClass.simpleName)
    }
}
