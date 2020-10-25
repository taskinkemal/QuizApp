package com.keplersegg.myself.helper

internal object Constant {

    object API {

        //private val _apiHost = "http://10.0.2.2:5001/"
        //private val _apiHost = "http://10.0.2.2/myself/"
        //private val _apiHost = "http://10.126.21.118/ccw/";
        private const val apiHost = "http://myself.keplersegg.com/api/"

        internal const val ApiRoot = apiHost + "api/"

        /* fun isEmulator(): Boolean {
            return (Build.FINGERPRINT.startsWith("generic")
                    || Build.FINGERPRINT.startsWith("unknown")
                    || Build.MODEL.contains("google_sdk")
                    || Build.MODEL.contains("Emulator")
                    || Build.MODEL.contains("Android SDK built for x86")
                    || Build.MANUFACTURER.contains("Genymotion")
                    || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                    || "google_sdk" == Build.PRODUCT)
        } */
    }

    const val isTest = true // to view test ads instead of production ads.
}