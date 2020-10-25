package com.keplersegg.myself.helper

class HttpResult<T>(var value: T?, var error: HttpError?) {

    fun hasError(): Boolean { return error != null }
}