package com.keplersegg.myself.fragments


import android.Manifest
import android.os.Bundle
import android.view.View
import com.keplersegg.myself.helper.AutoTaskType
import com.keplersegg.myself.R
import com.keplersegg.myself.helper.PermissionsHelper
import kotlinx.android.synthetic.main.fragment_auto_task_selector.*

class AutoTaskSelectorFragment : MasterFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.layout = R.layout.fragment_auto_task_selector
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lytCallDuration.setOnClickListener { selectAutoTaskType(AutoTaskType.CallDuration) }
        lytAppUsage.setOnClickListener { selectAutoTaskType(AutoTaskType.AppUsage) }
        lytWentTo.setOnClickListener { selectAutoTaskType(AutoTaskType.WentTo) }
    }

    override fun onResume() {
        super.onResume()
        SetTitle(R.string.lbl_auto_task_selector)
    }

    private fun NavigateToCallDuration() {
        activity.NavigateFragment(true, AddTaskFragment.newInstance(AutoTaskType.CallDuration, null))
    }

    private fun NavigateToAppUsage() {
        activity.NavigateFragment(true, AppUsageFragment.newInstance())
    }

    private fun selectAutoTaskType(type: AutoTaskType) {

        when (type) {

            AutoTaskType.CallDuration -> {

                PermissionsHelper.CheckPermission(activity, Manifest.permission.READ_PHONE_STATE, 1, Runnable {
                    NavigateToCallDuration()
                })
            }
            AutoTaskType.AppUsage -> {

                if (PermissionsHelper.CheckActionUsageSettingsPermission(activity)) {
                    NavigateToAppUsage()
                }

                //PermissionsHelper.CheckPermission(activity, Manifest.permission.PACKAGE_USAGE_STATS, 2, Runnable {
                //    NavigateToAppUsage()
                //})
            }
            AutoTaskType.WentTo -> activity.NavigateFragment(true, WentToFragment.newInstance())
        }
    }

    companion object {

        fun newInstance(): AutoTaskSelectorFragment {
            return AutoTaskSelectorFragment()
        }
    }
}
