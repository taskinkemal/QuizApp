package com.keplersegg.myself.interfaces

import com.keplersegg.myself.activities.MasterActivity
import com.keplersegg.myself.helper.AutoTaskType

interface ISyncTasksHost : ISyncHost {

    fun onSyncTasksSuccess(missingPermissions: List<AutoTaskType>)

    fun GetMasterActivity(): MasterActivity
}
