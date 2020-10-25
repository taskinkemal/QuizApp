package com.keplersegg.myself.fragments


import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.keplersegg.myself.helper.AutoTaskType
import com.keplersegg.myself.helper.ServiceMethods
import com.keplersegg.myself.models.ListItem
import kotlinx.android.synthetic.main.fragment_add_task.*

import com.keplersegg.myself.R
import com.keplersegg.myself.Room.Entity.Task
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import androidx.appcompat.app.AlertDialog


class AddTaskFragment : MasterFragment() {

    var TaskId = 0 // 0 means new task.
    var automationType: AutoTaskType? = null
    var automationData: ListItem? = null

    private var task: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.layout = R.layout.fragment_add_task

        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swcUnits!!.setOnCheckedChangeListener { _, isChecked ->
            SetUnitsVisibility(isChecked)
        }

        if (TaskId != 0) {

            doAsync { task = activity.AppDB().taskDao().get(TaskId)
                uiThread {
                    PrefillTask()
                    FillAutomationDetails(false)
                }
            }
        } else {

            SetUnitsVisibility(false)

            FillAutomationDetails(true)
        }
    }

    private fun FillAutomationDetails(autoFillTask: Boolean) {

        lblAutomationSubHeader.visibility = if (automationType != null) View.VISIBLE else View.GONE
        lytAutomation.visibility = if (automationType != null) View.VISIBLE else View.GONE
        lytAutomationVar.visibility = if (automationData != null) View.VISIBLE else View.GONE

        if (automationType != null) {

            var labelResource = 0

            when (automationType) {

                AutoTaskType.CallDuration -> { labelResource = R.string.autotask_callDuration }
                AutoTaskType.AppUsage -> { labelResource = R.string.autotask_appUsage }
                AutoTaskType.WentTo -> { labelResource = R.string.autotask_wentTo }
            }

            lblAutomationType.text = activity.getString(labelResource)

            if (automationData != null) {

                when (automationType) {

                    AutoTaskType.AppUsage -> { lblAutomationVarHeader.text = String.format("%s:", getString(R.string.lbl_application)) }
                    AutoTaskType.WentTo -> { lblAutomationVarHeader.text = String.format("%s:", getString(R.string.lbl_network)) }
                    else -> { }
                }

                lblAutomationVar.text = automationData!!.Label
            }

            if (autoFillTask) {

                when (automationType) {

                    AutoTaskType.CallDuration -> {
                        txtLabel.setText(activity.getString(R.string.autotask_callDuration))
                        swcUnits.isChecked = true
                        txtUnits.setText(R.string.lbl_minutes)
                        swcUnits.isEnabled = false
                        txtUnits.isEnabled = false
                    }
                    AutoTaskType.AppUsage -> {
                        txtLabel.setText(activity.getString(R.string.autotask_appUsage) + " " + lblAutomationVar.text)
                        swcUnits.isChecked = true
                        txtUnits.setText(R.string.lbl_minutes)
                        swcUnits.isEnabled = false
                        txtUnits.isEnabled = false
                    }
                    AutoTaskType.WentTo -> {
                        txtLabel.setText(activity.getString(R.string.autotask_wentTo) + " " + lblAutomationVar.text)
                        swcUnits.isChecked = false
                        swcUnits.isEnabled = false
                    }
                }
            }
        }
    }

    private fun SaveTask() {

        val label = txtLabel!!.text.toString()
        val dataType = if (swcUnits!!.isChecked) 1 else 0
        val unit = if (dataType == 1) txtUnits!!.text.toString() else ""
        val automationVar : String? = automationData?.ItemId

        if (label.isNotEmpty() &&
                (dataType == 0 || unit.isNotEmpty())) {

            doAsync {
                AddOrUpdateTask(label, dataType, unit, automationType, automationVar)
                uiThread {
                    val fragment = TasksPagerFragment.newInstance()
                    activity.NavigateFragment(true, fragment)
                }
            }
        } else {
            activity.showErrorMessage(activity.getString(R.string.err_add_task_label_required))
        }
    }

    private fun PrefillTask() {

        if (task != null) {

            txtLabel.setText(task!!.Label)
            swcUnits.isChecked = task!!.DataType == 1
            SetUnitsVisibility(task!!.DataType == 1)
            txtUnits.setText(task!!.Unit)

            if (task!!.AutomationType != null) {

                if (task!!.AutomationType!! > 0) {
                    GetAutomationDetails(AutoTaskType.valueOf(task!!.AutomationType!!)!!, task!!.AutomationVar)
                }
            }
        }
    }

    private fun GetAutomationDetails(automationType: AutoTaskType, automationVar: String?) {

        this.automationType = automationType

        when (automationType) {

            AutoTaskType.AppUsage -> {
                this.automationData = AppUsageFragment.getItemById(activity, automationVar!!)
            }
            AutoTaskType.WentTo -> {
                this.automationData = WentToFragment.getItemById(activity, automationVar!!)
            }
            else -> {
            }
        }
    }

    private fun AddOrUpdateTask(label: String, dataType: Int, unit: String, automationType: AutoTaskType?, automationVar: String?): Boolean {

        val automationTypeInt = if (automationType != null) automationType.typeId else null

        if (this.TaskId != 0) {

            if (this.activity.AppDB().taskDao().getCountByLabelExcludeId(TaskId, label) == 0) {

                task!!.Label = label
                task!!.DataType = dataType
                task!!.Unit = unit
                task!!.AutomationType = automationTypeInt
                task!!.AutomationVar = automationVar

                var canUpdate = true

                if (!activity.app.dataStore.getAccessToken().isNullOrBlank()) {
                    val taskId = ServiceMethods.uploadTask(activity, task!!)
                    canUpdate = !taskId.equals(-1)
                }

                if (canUpdate) {

                    activity.AppDB().taskDao().update(task!!)
                    return true
                }
                else
                    return false
            }
        } else {

            if (activity.AppDB().taskDao().getCountByLabel(label) == 0) {

                val newTask = Task.CreateItem(-1, label, dataType, unit, automationTypeInt, automationVar)

                val taskId: Int

                if (!activity.app.dataStore.getAccessToken().isNullOrBlank()) {
                    taskId = ServiceMethods.uploadTask(activity, newTask)
                }
                else {
                    taskId = this.activity.AppDB().taskDao().minId
                }

                if (taskId != -1) {
                    newTask.Id = taskId
                    activity.AppDB().taskDao().insert(newTask)
                    return true
                }
                else
                    return false
            }
        }

        return false
    }

    private fun SetUnitsVisibility(isVisible: Boolean) {

        tiUnits!!.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()
        SetTitle(R.string.lbl_task_details)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.menu_add_task, menu)
        super.onCreateOptionsMenu(menu, inflater)

        menu.findItem(R.id.menu_delete).isVisible = TaskId != 0
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_save -> SaveTask()
            R.id.menu_delete ->
            {
                if (task != null) {

                    AlertDialog.Builder(activity)
                            .setMessage(R.string.confirm_delete_task)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes) { _, _ ->
                                doAsync {
                                    activity.AppDB().taskDao().delete(task!!.Id)
                                    ServiceMethods.deleteTask(activity, task!!.Id)

                                    uiThread {
                                        activity.NavigateFragment(false, TasksPagerFragment.newInstance())
                                    }
                                }
                            }
                            .setNegativeButton(android.R.string.no, null).show()
                }
            }
        }
        return true

    }

    companion object {

        fun newInstance(taskId: Int): AddTaskFragment {

            val fragment = AddTaskFragment()

            fragment.TaskId = taskId

            return fragment
        }

        fun newInstance(automationType: AutoTaskType, automationData: ListItem?): AddTaskFragment {

            val fragment = AddTaskFragment()

            fragment.automationType = automationType
            fragment.automationData = automationData

            return fragment
        }
    }
}