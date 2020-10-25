package com.keplersegg.myself.async

import com.keplersegg.myself.Room.Entity.UserBadge
import com.keplersegg.myself.interfaces.ISyncHost
import org.jetbrains.anko.doAsync

open class SyncBadges(private var activity: ISyncHost) {

    fun upsertBadge(score: Int, badges: ArrayList<UserBadge>) {

        if (activity.GetApplication().user != null) {

            activity.GetApplication().user!!.Score = score

            for (i in 0 until badges.size) {

                doAsync {
                    val existingBadge = activity.AppDB().userBadgeDao()[badges[i].BadgeId]

                    val pushNewBadge = existingBadge != null && existingBadge.Level < badges[i].Level

                    val rowsAffected = activity.AppDB().userBadgeDao().update(badges[i])

                    if (rowsAffected == 0) {
                        activity.AppDB().userBadgeDao().insert(badges[i])
                        activity.GetApplication().dataStore.pushNewBadge(badges[i].BadgeId)
                    } else if (pushNewBadge) {
                        activity.GetApplication().dataStore.pushNewBadge(badges[i].BadgeId)
                    }
                }
            }
        }
    }
}