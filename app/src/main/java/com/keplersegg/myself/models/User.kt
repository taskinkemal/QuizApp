package com.keplersegg.myself.models

import com.facebook.AccessToken
import com.keplersegg.myself.Room.Entity.UserBadge

class User {

    var UserCode: String = "orifhgjtks" //TODO: unique user code will be generated on the server
    var FirstName: String? = null
    var LastName: String? = null
    var Email: String? = null
    var PictureUrl: String? = null
    var FacebookToken: AccessToken? = null
    var Score: Int = 0
    var Badges: ArrayList<UserBadge>? = null
}
