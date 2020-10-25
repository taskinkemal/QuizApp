package com.keplersegg.myself.interfaces

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.keplersegg.myself.activities.MasterActivity
import com.keplersegg.myself.models.User

interface ISignOut {

    fun onSignOut()

    fun GetUser(): User?

    fun GetMasterActivity(): MasterActivity

    fun GetGoogleSignInClient(): GoogleSignInClient
}
