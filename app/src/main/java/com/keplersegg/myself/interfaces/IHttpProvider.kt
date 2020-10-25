package com.keplersegg.myself.interfaces

import android.net.ConnectivityManager

interface IHttpProvider: IErrorMessage {

    fun getAccessToken(): String?

    fun getDeviceId(): String?

    fun getConnectivityManager(): ConnectivityManager
}