package com.keplersegg.myself.interfaces

interface IRefreshTokenHost : IHttpProvider, IErrorMessage {

    fun onRefreshSuccess()

    fun onRefreshError(message: String)

    fun setAccessToken(token: String)
}
