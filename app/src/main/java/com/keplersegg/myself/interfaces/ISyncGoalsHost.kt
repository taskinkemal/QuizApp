package com.keplersegg.myself.interfaces

import com.keplersegg.myself.MySelfApplication
import com.keplersegg.myself.Room.AppDatabase
import com.keplersegg.myself.Room.Entity.Goal

interface ISyncGoalsHost : ISyncHost {

    fun onSyncGoalsSuccess(list: List<Goal>)
}