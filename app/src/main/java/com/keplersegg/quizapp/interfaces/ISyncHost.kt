package com.keplersegg.myself.interfaces

import com.keplersegg.myself.MySelfApplication
import com.keplersegg.myself.Room.AppDatabase

interface ISyncHost : IHttpProvider {

    fun AppDB() : AppDatabase

    fun GetApplication() : MySelfApplication
}