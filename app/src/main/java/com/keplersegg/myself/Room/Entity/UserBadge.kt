package com.keplersegg.myself.Room.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class UserBadge {

    @PrimaryKey
    var BadgeId: Int = 0
    var Level: Int = 0

    companion object {

        fun CreateItem(badgeId: Int, level: Int): UserBadge {

            val m = UserBadge()

            m.BadgeId = badgeId
            m.Level = level

            return m
        }
    }
}