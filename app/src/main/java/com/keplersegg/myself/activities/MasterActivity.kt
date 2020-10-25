package com.keplersegg.myself.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.Toast

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.keplersegg.myself.interfaces.IHttpProvider
import com.keplersegg.myself.helper.TokenType
import com.keplersegg.myself.interfaces.IErrorMessage
import com.keplersegg.myself.models.User
import com.keplersegg.myself.MySelfApplication
import com.keplersegg.myself.R
import com.keplersegg.myself.Room.AppDatabase
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.crashlytics.android.Crashlytics
import com.google.firebase.analytics.FirebaseAnalytics
import com.keplersegg.myself.helper.AutoTaskType
import com.keplersegg.myself.helper.PermissionsHelper


@SuppressLint("Registered")
open class MasterActivity : AppCompatActivity(), IHttpProvider, IErrorMessage {

    lateinit var app: MySelfApplication
    lateinit var master: MasterActivity
    var mGoogleSigninClient: GoogleSignInClient? = null
    lateinit var firebaseAnalytics: FirebaseAnalytics


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        master = this
        app = application as MySelfApplication

        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = GetColor(R.color.colorPrimary)

        initializeGPlusSettings()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
    }

    override fun onResume() {
        super.onResume()

        logAnalyticsPageVisit()
    }

    private fun GetColor(id: Int): Int {
        return ContextCompat.getColor(this, id)
    }

    fun AppDB(): AppDatabase {
        return AppDatabase.getAppDatabase(this)!!
    }

    fun truncateDB(removeNonLocalsOnly: Boolean) {

        if (removeNonLocalsOnly) {
            AppDB().taskDao().deleteNonLocal() // this will also delete the entries.
        }
        else {
            AppDB().taskDao().deleteAll() // this will also delete the entries.
        }

        AppDB().userBadgeDao().deleteAll()
    }

    fun NavigateToActivity(activityName: String, clearTop: Boolean) {

        var i: Intent? = null

        when (activityName) {

            "Main" ->

                i = Intent(application, MainActivity::class.java)

            "Login" ->

                i = Intent(application, LoginActivity::class.java)

            else -> {
            }
        }

        if (i != null)
            NavigateToActivity(i, clearTop)
    }

    private fun NavigateToActivity(i: Intent?, clearTop: Boolean) {

        if (clearTop)
        // ana sayfadan geri dön'e tıklandığında çıkış yapma diyaloğu gelmesi için.
            i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)

        startActivity(i)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

        if (clearTop)
        // ana sayfadan geri dön'e tıklandığında çıkış yapma diyaloğu gelmesi için.
            finish()
    }

    private fun initializeGPlusSettings() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_client_id))
                //.requestServerAuthCode(getString(R.string.google_client_id))
                .requestEmail()
                .build()

        mGoogleSigninClient = GoogleSignIn.getClient(this, gso)
    }

    protected fun handleGoogleSignInResult(account: GoogleSignInAccount): Boolean {

        setToken(TokenType.Google, account.idToken)

        app.user = User()
        app.user!!.Email = account.email
        app.user!!.FirstName = account.givenName
        app.user!!.LastName = account.familyName
        val pictureUri = account.photoUrl
        if (pictureUri != null)
            app.user!!.PictureUrl = pictureUri.toString()

        return true
    }

    open fun setToken(tokenType: TokenType, token: String?) {

        when (tokenType) {

            TokenType.MySelf ->
            {
                app.dataStore.setAccessToken(token)

                if (token != null && token != "") {
                    logAnalyticsEvent(FirebaseAnalytics.Event.LOGIN, hashMapOf(FirebaseAnalytics.Param.SOURCE to "MySelf"))
                }
            }

            TokenType.Facebook ->
            {
                app.dataStore.setFacebookToken(token)

                if (token != null && token != "") {
                    logAnalyticsEvent(FirebaseAnalytics.Event.LOGIN, hashMapOf(FirebaseAnalytics.Param.SOURCE to "Facebook"))
                }
            }

            TokenType.Google ->
            {
                app.dataStore.setGoogleToken(token)

                if (token != null && token != "") {
                    logAnalyticsEvent(FirebaseAnalytics.Event.LOGIN, hashMapOf(FirebaseAnalytics.Param.SOURCE to "Google"))
                }
            }

        }
    }

    override fun getConnectivityManager(): ConnectivityManager {
        return getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    override fun getAccessToken(): String? {
        return app.dataStore.getAccessToken()
    }

    override fun getDeviceId(): String? {
        return app.dataStore.getRegisterID()
    }

    override fun showErrorMessage(message: String) {

        Crashlytics.log(message)
        runOnUiThread {

            if (this is MainActivity) {
                this.showSnackbarMessage(message)
            }
            else {
                Toast.makeText(this@MasterActivity, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun logException(exception: Exception, message: String) {

        Crashlytics.logException(exception)
        showErrorMessage(message)
    }

    fun GetMasterActivity() : MasterActivity {
        return this
    }

    fun requestPermissions(missingPermissions: List<AutoTaskType>) {

        for (taskType in missingPermissions) {

            when (taskType) {
                AutoTaskType.CallDuration -> {
                    PermissionsHelper.CheckPermission(this, Manifest.permission.READ_PHONE_STATE, 1, Runnable { })
                }
                AutoTaskType.AppUsage -> {

                    PermissionsHelper.CheckActionUsageSettingsPermission(this)

                    //if (PermissionsHelper.CheckActionUsageSettingsPermission(this)) {
                    //    return
                    //}

                    //PermissionsHelper.CheckPermission(this, Manifest.permission.PACKAGE_USAGE_STATS, 2, Runnable { })
                }
                AutoTaskType.WentTo -> {

                }
            }
        }
    }

    private fun logAnalyticsEvent(eventType: String, params: HashMap<String, String>?) {

        val bundle = Bundle()
        if (params != null) {
            for (p in params) {
                bundle.putString(p.key, p.value)
            }
        }

        firebaseAnalytics.logEvent(eventType, bundle) //FirebaseAnalytics.Event
    }

    private fun logAnalyticsPageVisit() {

        firebaseAnalytics.setCurrentScreen(this, javaClass.simpleName, javaClass.simpleName)
    }

    fun toggleProgressBar(lytProgressBar: RelativeLayout, isVisible: Boolean) {

        if (isVisible) {

            lytProgressBar.visibility = View.VISIBLE
        }
        else {

            lytProgressBar.visibility = View.GONE
        }
    }
}
