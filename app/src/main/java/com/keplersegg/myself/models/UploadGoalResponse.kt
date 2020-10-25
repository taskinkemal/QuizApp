package com.keplersegg.myself.models

import com.keplersegg.myself.Room.Entity.UserBadge

class UploadGoalResponse {

    var GoalId: Int = 0
    var Score: Int = 0
    var NewBadges: ArrayList<UserBadge> = ArrayList()
}