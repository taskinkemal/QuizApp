package com.keplersegg.myself.fragments


import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import com.crashlytics.android.Crashlytics

import com.keplersegg.myself.R
import com.keplersegg.myself.Room.Entity.Goal
import com.keplersegg.myself.Room.Entity.Task
import com.keplersegg.myself.adapters.TaskSpinnerAdapter
import com.keplersegg.myself.helper.ServiceMethods
import kotlinx.android.synthetic.main.fragment_add_goal.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*
import kotlin.collections.ArrayList
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.TextView
import com.keplersegg.myself.async.SyncBadges
import com.keplersegg.myself.helper.Utils
import java.text.SimpleDateFormat


class AddGoalFragment : MasterFragment() {

    var TaskId: Int = -1
    var GoalId: Int = -1
    private lateinit var goal: Goal
    private var task: Task? = null
    private lateinit var allTasks: ArrayList<Task>

    private lateinit var spnTasksAdapter: TaskSpinnerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.layout = R.layout.fragment_add_goal

        setHasOptionsMenu(true)
    }

    fun setDate(tag: String?, day: Int) {

        if (tag == "start") {

            goal.StartDay = day
            setDateText(lblStartDay, day)
        }
        else if (tag == "end") {

            goal.EndDay = day
            setDateText(lblEndDay, day)
        }
    }

    private fun setDateText(txt: TextView, day: Int) {

        if (day > 0) {
            val cal = Utils.getDate(day)
            val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            txt.text = sdf.format(cal.time)
        }
        else {
            txt.text = ""
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        allTasks = ArrayList()
        spnTasksAdapter = TaskSpinnerAdapter(activity, android.R.layout.simple_spinner_item, allTasks)

        spnTask.adapter = spnTasksAdapter
        spnTask.onItemSelectedListener = object : OnItemSelectedListener {

            override fun onItemSelected(adapterView: AdapterView<*>, view: View,
                                        position: Int, id: Long) {
                // Here you get the current item (a User object) that is selected by its position
                task = spnTasksAdapter.getItem(position)!!
                TaskId = task!!.Id

                initTaskRelatedFields()
            }

            override fun onNothingSelected(adapter: AdapterView<*>) {}
        }

        lblStartDay.setOnClickListener {

            val newFragment = DatePickerFragment()
            newFragment.fragment = this
            if (goal.StartDay != 0) {
                newFragment.preselectDate = goal.StartDay
            }
            newFragment.show(childFragmentManager, "start")
        }

        lblEndDay.setOnClickListener {

            val newFragment = DatePickerFragment()
            newFragment.fragment = this
            if (goal.EndDay != 0) {
                newFragment.preselectDate = goal.EndDay
            }
            newFragment.show(childFragmentManager, "end")
        }

        if (GoalId != -1) {

            doAsync {

                goal = activity.AppDB().goalDao()[GoalId]
                TaskId = goal.TaskId
                task = activity.AppDB().taskDao()[goal.TaskId]

                allTasks.clear()
                allTasks.addAll(activity.AppDB().taskDao().getAll(1))

                uiThread {
                    spnTasksAdapter.notifyDataSetChanged()
                    PrefillGoal()
                }
            }
        }
        else {

            goal = Goal.CreateItem(-1, TaskId, 1, 0, 0, 0)

            doAsync {

                task = activity.AppDB().taskDao().get(TaskId)

                allTasks.clear()
                allTasks.addAll(activity.AppDB().taskDao().getAll(1))

                uiThread {
                    spnTasksAdapter.notifyDataSetChanged()
                    PrefillGoal()
                }
            }
        }
    }

    private fun PrefillGoal() {

        txtTarget.setText(goal.Target.toString())
        setDateText(lblStartDay, goal.StartDay)
        setDateText(lblEndDay, goal.EndDay)

        initTaskRelatedFields()
    }

    private fun initTaskRelatedFields() {

        lblUnits.text = if (task != null && task?.Unit != "") task!!.Unit else getString(R.string.lbl_items)
        setGoalMinMaxSource(task?.AutomationType != null && task!!.AutomationType!! > 0)
        if (task != null) {
            spnTask.setSelection(allTasks.indexOfFirst { t -> t.Id == task!!.Id })
        }
    }

    private fun setGoalMinMaxSource(isAutomatedTask: Boolean) {

        val resourceId = if (isAutomatedTask) R.array.spinner_goalMinMax_AutomatedTask else R.array.spinner_goalMinMax

        val spinnerArrayAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item,
                activity.resources.getStringArray(resourceId))
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item)
        spnGoalMinMax.adapter = spinnerArrayAdapter
    }

    private fun GetGoalMinMaxValue(isAutomatedTask: Boolean) : Int {

        val position = spnGoalMinMax!!.selectedItemPosition
        val resourceId = if (isAutomatedTask) R.array.spinner_goalMinMax_values_AutomatedTask else R.array.spinner_goalMinMax_values
        return Arrays.asList(*activity.resources.getStringArray(resourceId))[position].toInt()
    }

    private fun ConvertToInteger(text: String): Int {

        var result = -1

        try {
            result = Integer.parseInt(text)
        } catch (nfe: NumberFormatException) {
            Crashlytics.logException(nfe)
        }

        return result
    }

    private fun SaveGoal() {

        if (task == null) {

            showErrorMessage(getString(R.string.err_add_goal_task_required))
            return
        }

        val isAutomatedTask = task!!.AutomationType != null && task!!.AutomationType!! > 0
        val minMax = GetGoalMinMaxValue(isAutomatedTask)
        val target = ConvertToInteger(txtTarget!!.text.toString())
        val startDay = goal.StartDay // this is updated after datepicker
        val endDay = goal.EndDay // this is updated after datepicker

        if (target < 0 || target == 0 && !isAutomatedTask) {

            showErrorMessage(getString(R.string.err_add_goal_target_value_required))
            return
        }

        if (endDay - startDay < 1) {

            showErrorMessage(getString(R.string.err_add_goal_valid_interval_required))
            return
        }

        if (startDay < Utils.getToday()) {

            showErrorMessage(getString(R.string.err_add_goal_valid_startDate))
            return
        }

        doAsync {
            AddOrUpdateGoal(minMax, target, startDay, endDay)
            uiThread {
                val fragment = GoalsFragment.newInstance()
                activity.NavigateFragment(true, fragment)
            }
        }
    }

    private fun AddOrUpdateGoal(minMax: Int, target: Int, startDay: Int, endDay: Int): Boolean {

        if (this.GoalId != -1) {

            goal.TaskId = TaskId
            goal.MinMax = minMax
            goal.Target = target
            goal.StartDay = startDay
            goal.EndDay = endDay

            var canUpdate = true

            if (!activity.app.dataStore.getAccessToken().isNullOrBlank()) {
                val response = ServiceMethods.uploadGoal(activity, goal)
                canUpdate = response != null

                if (response != null) {
                    SyncBadges(activity).upsertBadge(response.Score, response.NewBadges)
                    activity.showNewBadgeDialog()
                }
            }

            if (canUpdate) {

                activity.AppDB().goalDao().update(goal)
                return true
            }
            else
                return false

        } else {

            val newGoal = Goal.CreateItem(-1, TaskId, minMax, target, startDay, endDay)

            var goalId: Int = -1

            if (!activity.app.dataStore.getAccessToken().isNullOrBlank()) {
                val response = ServiceMethods.uploadGoal(activity, newGoal)
                if (response != null) {
                    goalId = response.GoalId
                    SyncBadges(activity).upsertBadge(response.Score, response.NewBadges)
                    activity.showNewBadgeDialog()
                }
            }
            else {
                goalId = this.activity.AppDB().goalDao().minId
                if (goalId > -2) {
                    goalId = -2
                }
            }

            if (goalId != -1) {
                newGoal.Id = goalId
                activity.AppDB().goalDao().insert(newGoal)
                return true
            }
            else
                return false
        }
    }

    override fun onResume() {
        super.onResume()
        SetTitle(R.string.lbl_goal_details)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.menu_add_task, menu)
        super.onCreateOptionsMenu(menu, inflater)

        menu.findItem(R.id.menu_delete).isVisible = GoalId != -1
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_save -> SaveGoal()
            R.id.menu_delete ->
            {
                if (GoalId != -1) {

                    AlertDialog.Builder(activity)
                            .setMessage(R.string.confirm_delete_goal)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes) { _, _ ->
                                doAsync {
                                    activity.AppDB().goalDao().delete(goal)
                                    ServiceMethods.deleteGoal(activity, goal.Id)

                                    uiThread {
                                        activity.NavigateFragment(false, GoalsFragment.newInstance())
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

        fun newInstance(taskId: Int, goalId: Int): AddGoalFragment {

            val fragment = AddGoalFragment()

            fragment.TaskId = taskId
            fragment.GoalId = goalId

            return fragment
        }
    }
}
