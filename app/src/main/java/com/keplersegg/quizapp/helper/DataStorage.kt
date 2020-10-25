package com.keplersegg.myself.helper

import android.content.Context
import android.content.SharedPreferences
import com.keplersegg.myself.MySelfApplication
import java.util.*

class DataStorage(app: MySelfApplication) {

    private val preferences: SharedPreferences

    private val preferencesKey = "MyselfPreferences"
    private val accessToken = "accessToken"
    private val facebookToken = "facebookToken"
    private val googleToken = "googleToken"
    private val deviceRegistrationID = "deviceRegistrationID"
    private val newBadges = "newBadges"
    private val tutorialDone = "tutorialDone"
    private val callDuration = "callDuration"

    init {

        preferences = app.getSharedPreferences(preferencesKey, Context.MODE_PRIVATE)
    }

    fun getAccessToken(): String? { return getString(accessToken) }
    fun getFacebookToken(): String? { return getString(facebookToken) }
    fun getGoogleToken(): String? { return getString(googleToken) }
    fun getRegisterID(): String? {

        var registerID = getString(deviceRegistrationID)

        if (registerID.isNullOrBlank()) {

            registerID = UUID.randomUUID().toString()
            setValue(deviceRegistrationID, registerID)
        }

        return registerID
    }
    fun getTutorialDone() : Boolean { return getBoolean(tutorialDone) }
    fun getTotalCallDuration(day: Int) : Long {

        val data = getString(callDuration)

        if (data != null && data.contains(':')) {
            val splitted = data.split(':')
            if (splitted.size == 2 && Integer.getInteger(splitted[0], 0) == day) {
                val result = Integer.getInteger(splitted[1], 0)
                return if (result != null) result.toLong() else 0
            }
        }

        setTotalCallDuration(day, 0)
        return 0
    }

    fun setAccessToken(token: String?) { setValue(accessToken, token) }
    fun setFacebookToken(token: String?) { setValue(facebookToken, token) }
    fun setGoogleToken(token: String?) { setValue(googleToken, token) }
    fun setTutorialDone() { setBoolean(tutorialDone, true) }
    fun setTotalCallDuration(day: Int, totalDuration: Long) { setValue(callDuration, "$day:$totalDuration") }

    private fun setValue(key: String, value: String?) {

        val editor = preferences.edit()

        editor.putString(key, value)

        editor.apply()
    }

    private fun setBoolean(key: String, value: Boolean) {

        val editor = preferences.edit()

        editor.putBoolean(key, value)

        editor.apply()
    }

    private fun getString(key: String): String? {

        return if (preferences.contains(key))
            preferences.getString(key, null)
        else
            null
    }

    private fun getBoolean(key: String): Boolean {

        return if (preferences.contains(key))
            preferences.getBoolean(key, false)
        else
            false
    }

    fun popNewBadge(): Int? {

        if (preferences.contains(newBadges)) {
            val list = preferences.getStringSet(newBadges, null)
            if (list != null) {
                val result = list.firstOrNull()

                if (result != null) {
                    val badgeId = result.toInt()
                    list.remove(result)
                    pushNewBadges(list)
                    return badgeId
                }
            }
        }
        return null
    }

    fun pushNewBadge(badgeId: Int) {

        if (!preferences.contains(newBadges)) {
            pushNewBadges(hashSetOf())
        }

        val list = preferences.getStringSet(newBadges, null)
        list!!.add(badgeId.toString())
        pushNewBadges(list)
    }

    private fun pushNewBadges(list: MutableSet<String>) {

        val editor = preferences.edit()
        editor.putStringSet(newBadges, list)
        editor.apply()
    }
}