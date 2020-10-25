package com.keplersegg.myself.activities

import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ProgressBar

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.keplersegg.myself.async.GetFacebookUser
import com.keplersegg.myself.interfaces.ILoginHost
import com.keplersegg.myself.interfaces.IRefreshTokenHost
import com.keplersegg.myself.interfaces.ISetFacebookUser
import com.keplersegg.myself.async.LoginTask
import com.keplersegg.myself.async.RefreshTokenTask
import com.keplersegg.myself.helper.TokenType
import com.keplersegg.myself.models.User
import com.keplersegg.myself.R
import kotlinx.android.synthetic.main.activity_loader.*
import android.app.job.JobScheduler
import android.content.Context
import com.keplersegg.myself.services.AutomatedTaskService
import android.app.job.JobInfo
import android.content.ComponentName
import com.keplersegg.myself.MySelfApplication
import com.keplersegg.myself.async.SyncTasks
import com.keplersegg.myself.helper.AutoTaskType
import com.keplersegg.myself.interfaces.ISyncTasksHost


class LoaderActivity : MasterActivity(), ISetFacebookUser, ILoginHost, IRefreshTokenHost, ISyncTasksHost {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loader)

        val ab = master.actionBar
        ab?.hide()

        Glide.with(this)
                .load(R.drawable.app_background)
                .apply(RequestOptions.centerCropTransform())
                .into(imgLoginBackground)

        RegisterAutomatedTaskService()
    }

    private fun loginCheck() {

        val accessToken = app.dataStore.getAccessToken()

        if (LoginCheckSocialInternal()) {

        }
        else if (accessToken != null && accessToken.isNotEmpty()) {

            RefreshTokenTask(this).execute(accessToken, app.dataStore.getRegisterID())
        } else {
            clearToken()
        }
    }

    private fun LoginCheckSocialInternal() : Boolean {

        val account = GoogleSignIn.getLastSignedInAccount(this)

        if (account != null) {
            if (handleGoogleSignInResult(account)) {

                LoginTask(this).Run(TokenType.Google, app.dataStore.getGoogleToken(),
                        app.user!!.Email,
                        app.user!!.FirstName,
                        app.user!!.LastName,
                        app.user!!.PictureUrl)
                return true
            }
        }

        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired

        if (isLoggedIn) {

            GetFacebookUser().Run(this, accessToken)
            return true
        }

        return false
    }

    private fun LoginCheckSocial() {

        if (LoginCheckSocialInternal())
            return

        clearToken()
    }

    private fun clearToken() {

        app.user = null
        app.dataStore.setAccessToken(null)
        NavigateToActivity("Login", true)
    }

    override fun onResume() {
        super.onResume()

        FadeinView(prgBarLoader)
    }

    private fun FadeinView(prgBarLoader: ProgressBar) {

        val anim = AnimationUtils.loadAnimation(this, R.anim.loader)


        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {

                loginCheck()
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })

        prgBarLoader.startAnimation(anim)
    }

    override fun onSetFacebookUser(user: User?, token: String?) {

        if (user != null) {

            setToken(TokenType.Facebook, token)
        }

        setUser(user, TokenType.Facebook)
    }

    private fun setUser(user: User?, tokenType: TokenType) {

        app.user = user

        if (user != null) {

            val token = if (tokenType == TokenType.Facebook)
                app.dataStore.getFacebookToken()
            else app.dataStore.getGoogleToken()

            LoginTask(this).Run(tokenType,
                    token,
                    app.user!!.Email,
                    app.user!!.FirstName,
                    app.user!!.LastName,
                    app.user!!.PictureUrl)
        } else {

            app.dataStore.setAccessToken(null)
            NavigateToActivity("Login", true)
        }
    }

    override fun onLoginSuccess() {

        SyncTasks(this).execute()
    }

    override fun onLoginError(message: String) {

        app.user = null
        app.dataStore.setAccessToken(null)
        showErrorMessage(message)
        NavigateToActivity("Login", true)
    }

    override fun setAccessToken(token: String) {

        setToken(TokenType.MySelf, token)
    }

    override fun onRefreshSuccess() {
        SyncTasks(this).execute()
    }

    override fun onRefreshError(message: String) {
        LoginCheckSocial()
    }

    private fun RegisterAutomatedTaskService() {

        val jobScheduler = applicationContext
                .getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        val componentName = ComponentName(this, AutomatedTaskService::class.java)

        val jobInfo = JobInfo.Builder(1, componentName)
                .setPeriodic(15 * 60 * 1000, 5000)
                .setPersisted(true)
                .build()

        jobScheduler.schedule(jobInfo)
    }

    override fun onSyncTasksSuccess(missingPermissions: List<AutoTaskType>) {

        requestPermissions(missingPermissions)
        goToMain()
    }

    override fun GetApplication(): MySelfApplication {
        return app
    }

    private fun goToMain() {
        //AutoTasksManager().Run(applicationContext, Runnable { })
        NavigateToActivity("Main", true)
    }
}
