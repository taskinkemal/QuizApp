package com.keplersegg.myself.interfaces

interface ILoginHost : IHttpProvider, IErrorMessage {

    fun onLoginSuccess()

    fun onLoginError(message: String)

    fun setAccessToken(token: String)
}
