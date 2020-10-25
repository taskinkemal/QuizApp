package com.keplersegg.quizapp.interfaces

import com.keplersegg.quizapp.activities.MasterActivity
import com.keplersegg.quizapp.helper.AutoTaskType

interface ISyncTasksHost : ISyncHost {

    fun onSyncTasksSuccess(missingPermissions: List<AutoTaskType>)

    fun GetMasterActivity(): MasterActivity
}
