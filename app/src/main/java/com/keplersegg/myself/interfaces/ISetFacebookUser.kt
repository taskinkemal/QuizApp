package com.keplersegg.myself.interfaces

import com.keplersegg.myself.models.User

interface ISetFacebookUser : IErrorMessage {

    fun onSetFacebookUser(user: User?, token: String?)
}
