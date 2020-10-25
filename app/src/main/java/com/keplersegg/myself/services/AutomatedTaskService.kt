package com.keplersegg.myself.services

import android.app.job.JobParameters
import android.app.job.JobService
import com.keplersegg.myself.helper.AutoTasksManager


class AutomatedTaskService: JobService() {

    override fun onStopJob(params: JobParameters?): Boolean {

        return false
    }

    override fun onStartJob(params: JobParameters?): Boolean {

        AutoTasksManager().Run(applicationContext, Runnable { jobFinished(params, true) })

        return false
    }
}