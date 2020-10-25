package com.keplersegg.myself.fragments


import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.keplersegg.myself.activities.MasterActivity
import kotlinx.android.synthetic.main.fragment_profile.*

import com.keplersegg.myself.interfaces.ISignOut
import com.keplersegg.myself.async.SignOut
import com.keplersegg.myself.models.User
import com.keplersegg.myself.R
import com.keplersegg.myself.helper.ServiceMethods
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread




class ProfileFragment : MasterFragment(), ISignOut {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.layout = R.layout.fragment_profile
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtLogin.setOnClickListener { activity.NavigateToActivity("Login", true) }
        txtLogout.setOnClickListener { SignOut().Run(this@ProfileFragment) }

        txtLogin.visibility = if (activity.app.user == null) View.VISIBLE else View.GONE
        txtLogout.visibility = if (activity.app.user != null) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()
        SetTitle(R.string.lbl_profile)

        if (activity.app.user != null) {
            lblUserName!!.text = activity.app.user!!.FirstName + " " + activity.app.user!!.LastName

            doAsync {

                val user = ServiceMethods.getUser(activity)
                activity.app.user!!.Score = user!!.Score

                if (user.Badges != null) {
                    for (s in user.Badges!!) {

                        val localBadge = activity.AppDB().userBadgeDao().get(s.BadgeId)

                        if (localBadge == null) {

                            activity.AppDB().userBadgeDao().insert(s)
                        }
                        else if (s.Level > localBadge.Level) {

                            activity.AppDB().userBadgeDao().update(s)
                        }
                    }
                }

                val allBadges = activity.AppDB().userBadgeDao().all

                uiThread {

                    lblScore!!.text = activity.app.user!!.Score.toString()

                    bvBadge1.setLevel(0)
                    bvBadge2.setLevel(0)
                    bvBadge3.setLevel(0)

                    for (b in allBadges) {

                        when {
                            b.BadgeId == 1 -> bvBadge1.setLevel(b.Level)
                            b.BadgeId == 2 -> bvBadge2.setLevel(b.Level)
                            b.BadgeId == 3 -> bvBadge3.setLevel(b.Level)
                        }
                    }
                }
            }
        }
        else {
            lblUserName!!.text = activity.getString(R.string.lbl_guest)
            lblScore!!.text = "0"
            bvBadge1.setLevel(0)
            bvBadge2.setLevel(0)
            bvBadge3.setLevel(0)
        }
        if (!activity.app.user?.PictureUrl.isNullOrBlank()) {

            Glide.with(activity)
                    .load(activity.app.user!!.PictureUrl)
                    .apply(RequestOptions()
                            .placeholder(R.drawable.ic_baseline_account_circle_24px)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                    .apply(RequestOptions.circleCropTransform())
                    .into(imgUserPicture)
        }
        else {

            Glide.with(activity)
                    .load(R.drawable.ic_baseline_account_circle_24px)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imgUserPicture)
        }
    }

    override fun onSignOut() {

        doAsync {

            activity.truncateDB(false)

            uiThread {
                activity.app.clearSession()
                activity.NavigateToActivity("Login", true)
            }
        }
    }

    override fun GetUser(): User? {
        return activity.app.user
    }

    override fun GetGoogleSignInClient(): GoogleSignInClient {
        return activity.mGoogleSigninClient!!
    }

    override fun GetMasterActivity() : MasterActivity {
        return activity
    }

    companion object {

        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }
}
