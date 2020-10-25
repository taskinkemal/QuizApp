package com.keplersegg.myself.helper

import android.widget.RelativeLayout
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.keplersegg.myself.R
import com.keplersegg.myself.activities.MainActivity


object AdMobHelper {

    fun initAdUnit(activity: MainActivity, wrapper: RelativeLayout, unitId: Int) {

        val mAdView = AdView(activity)
        mAdView.adSize = AdSize.BANNER
        mAdView.adUnitId = if (Constant.isTest) activity.getString(R.string.adTest) else activity.getString(unitId)
        wrapper.addView(mAdView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }
}