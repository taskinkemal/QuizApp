package com.keplersegg.myself.helper

import com.google.gson.GsonBuilder
import com.keplersegg.myself.interfaces.IHttpProvider

import org.json.JSONObject

import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.reflect.KClass


object HttpClient {

    private fun isNetworkAvailable(provider: IHttpProvider): Boolean {

        return provider.getConnectivityManager().activeNetworkInfo != null
    }

    fun hasInternetAccess(provider: IHttpProvider): Boolean {

        return isNetworkAvailable(provider)
    }

    inline fun <reified T: Any> send(classDef: KClass<T>, provider: IHttpProvider, url: String, method: String, objParam: JSONObject?): HttpResult<T> {

        val result = HttpResult<T>(null, null)

        val accessToken = provider.getAccessToken()

        var responseString = ""
        var errorString = ""

        try {

            if (!hasInternetAccess(provider)) {

                result.error = HttpError(503, "InternetConnectionError", "")

            } else {

                val _url = URL(getAbsoluteUrl(url))
                val client = _url.openConnection() as HttpURLConnection

                client.requestMethod = method.toUpperCase()
                //client.setDoOutput(true);
                client.instanceFollowRedirects = false

                if (accessToken != null) {

                    client.setRequestProperty("Authorization", "Bearer " + accessToken)
                    //client.setRequestProperty("Accept-Language", headers!!.GetCulture())
                }
                client.setRequestProperty("Accept", "application/json")
                client.setRequestProperty("Content-type", "application/json")

                client.useCaches = false

                if (objParam != null) {

                    val bytes = objParam.toString().toByteArray(charset("UTF-8"))

                    client.outputStream.write(bytes)
                }

                val responseCode = client.responseCode

                if (responseCode == 200) {

                    responseString = readStream(client.inputStream)

                    val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()

                    if (classDef.java.isArray) {

                        val responseObject = JSONObject(responseString)

                        if (responseObject.has("Items")) {

                            result.value = gson.fromJson<T>(responseObject.getJSONArray("Items").toString(), T::class.java)
                        }
                        else {
                            result.error = HttpError(504, "MissingData", "Items is missing.")
                        }
                    }
                    else {
                        result.value = gson.fromJson(responseString, T::class.java)
                    }

                } else {

                    errorString = readStream(client.errorStream)
                    val responseObject = JSONObject(errorString)

                    result.error = HttpError(
                            if (responseObject.has("Code")) responseObject.getInt("Code") else 504,
                            if (responseObject.has("Phrase")) responseObject.getString("Phrase") else "GeneralError",
                            if (responseObject.has("Message")) responseObject.getString("Message") else ""
                    )
                }
            }
        } catch (exc: Exception) {

            result.error = HttpError(504, "GeneralError", if (exc.message != null) exc.message!! else "")

            var log = "responseString:$responseString errorString:$errorString"
            log += " url:$url"
            log += " method:$method"

            provider.logException(Exception(log, exc), "HttpClient")
        }

        return result
    }

    fun send(provider: IHttpProvider, url: String, method: String, objParam: JSONObject?): JSONObject? {

        val accessToken = provider.getAccessToken()
        var responseObject: JSONObject?

        var responseString = ""
        var errorString = ""

        try {

            if (!hasInternetAccess(provider)) {

                responseObject = JSONObject()
                responseObject.put("Code", 503)
                responseObject.put("Phrase", "InternetConnectionError")
                responseObject.put("Message", "")

            } else {

                val _url = URL(getAbsoluteUrl(url))
                val client = _url.openConnection() as HttpURLConnection

                client.requestMethod = method.toUpperCase()
                //client.setDoOutput(true);
                client.instanceFollowRedirects = false

                if (accessToken != null) {

                    client.setRequestProperty("Authorization", "Bearer " + accessToken)
                    //client.setRequestProperty("Accept-Language", headers!!.GetCulture())
                }
                client.setRequestProperty("Accept", "application/json")
                client.setRequestProperty("Content-type", "application/json")

                client.useCaches = false

                if (objParam != null) {

                    val bytes = objParam.toString().toByteArray(charset("UTF-8"))

                    client.outputStream.write(bytes)
                }

                val responseCode = client.responseCode

                if (responseCode == 200) {

                    responseString = readStream(client.inputStream)
                    responseObject = JSONObject(responseString)

                } else {

                    errorString = readStream(client.errorStream)
                    responseObject = JSONObject(errorString)
                }
            }
        } catch (exc: Exception) {

            responseObject = JSONObject()
            responseObject.put("Code", 504)
            responseObject.put("Phrase", "GeneralError")
            responseObject.put("Message", exc.message)

            var log = "responseString:$responseString errorString:$errorString"
            log += " url:$url"
            log += " method:$method"

            provider.logException(Exception(log, exc), "HttpClient")
        }

        return responseObject
    }

    fun getAbsoluteUrl(relativeUrl: String): String {

        return Constant.API.ApiRoot + relativeUrl
    }

    fun readStream(stream: InputStream): String {

        return stream.bufferedReader().use { it.readText() }
    }
}
