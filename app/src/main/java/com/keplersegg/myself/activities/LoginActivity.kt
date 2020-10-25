package com.keplersegg.myself.activities

import android.content.Intent
import android.os.Bundle
import android.view.Window

import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.keplersegg.myself.MySelfApplication
import com.keplersegg.myself.async.GetFacebookUser
import com.keplersegg.myself.interfaces.ILoginHost
import com.keplersegg.myself.interfaces.ISetFacebookUser
import com.keplersegg.myself.interfaces.ISyncTasksHost
import com.keplersegg.myself.async.LoginTask
import com.keplersegg.myself.async.SyncTasks
import com.keplersegg.myself.helper.AutoTasksManager
import com.keplersegg.myself.helper.TokenType
import com.keplersegg.myself.models.User
import com.keplersegg.myself.R
import com.keplersegg.myself.helper.AutoTaskType
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.doAsync

import java.util.Arrays


class LoginActivity : MasterActivity(), ISetFacebookUser, ILoginHost, ISyncTasksHost {

    override fun GetApplication(): MySelfApplication {
        return app
    }

    private var fbLoginManager: LoginManager? = null
    private var fbCallbackManager: CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(R.layout.activity_login)

        fbLoginManager = LoginManager.getInstance()
        fbCallbackManager = CallbackManager.Factory.create()
        fbLoginManager!!.registerCallback(fbCallbackManager!!, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                // here write code When Login successfully

                GetFacebookUser().Run(this@LoginActivity, loginResult.accessToken)
            }

            override fun onCancel() {
                toggleProgressBar(lytProgressBar, false)
            }

            override fun onError(e: FacebookException) {
                logException(e, "Facebook login error")
                toggleProgressBar(lytProgressBar, false)
            }
        })

        txtContinueWithoutAccount.setOnClickListener {

            doAsync {

                truncateDB(true)
                app.clearSession()
                SyncTasks(this@LoginActivity).execute()
            }
        }

        btnLoginFacebook.setOnClickListener {
            toggleProgressBar(lytProgressBar, true)
            fbLoginManager!!.logInWithReadPermissions(this@LoginActivity, Arrays.asList("email", "public_profile", "user_birthday"))
        }

        btnLoginGoogle.setOnClickListener {
            toggleProgressBar(lytProgressBar, true)
            val signInIntent = mGoogleSigninClient!!.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == RC_SIGN_IN) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGoogleSignInResult(task.result!!)
            setUser(app.user, TokenType.Google)

        } else {
            super.onActivityResult(requestCode, resultCode, data)
            fbCallbackManager!!.onActivityResult(requestCode, resultCode, data)
        }
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

            toggleProgressBar(lytProgressBar, false)

            when (tokenType) {
                TokenType.Facebook -> showErrorMessage(getString(R.string.err_login_facebook))
                TokenType.Google -> showErrorMessage(getString(R.string.err_login_google))
                else -> showErrorMessage(getString(R.string.err_login_user))
            }
        }
    }

    override fun onLoginSuccess() {

        SyncTasks(this).execute()
    }

    override fun onLoginError(message: String) {

        app.user = null
        toggleProgressBar(lytProgressBar, false)
        showErrorMessage(message)
    }

    override fun setAccessToken(token: String) {

        setToken(TokenType.MySelf, token)
    }

    override fun onSyncTasksSuccess(missingPermissions: List<AutoTaskType>) {

        requestPermissions(missingPermissions)
        AutoTasksManager().Run(applicationContext, Runnable { })
        NavigateToActivity("Main", true)
    }

    companion object {
        private const val RC_SIGN_IN = 430
    }
}
