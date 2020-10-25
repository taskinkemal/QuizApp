package com.keplersegg.myself.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.keplersegg.myself.adapters.TasksAdapter
import com.keplersegg.myself.async.GetTaskEntries
import com.keplersegg.myself.interfaces.IGetTasksHost
import com.keplersegg.myself.R
import com.keplersegg.myself.Room.Entity.TaskEntry
import com.keplersegg.myself.helper.AdMobHelper
import kotlinx.android.synthetic.main.fragment_tasks.*


class TasksFragment : MasterFragment(), IGetTasksHost {

    private var adapter: TasksAdapter? = null
    private var rcylTasks: RecyclerView? = null
    private var day = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.layout = R.layout.fragment_tasks
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (rootView != null)
        {
            return rootView
        }

        rootView = super.onCreateView(inflater, container, savedInstanceState)

        rcylTasks = rootView!!.findViewById(R.id.rcylTasks)

        adapter = TasksAdapter(activity, day)
        rcylTasks!!.adapter = adapter
        rcylTasks!!.layoutManager = LinearLayoutManager(activity)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter!!.notifyDataSetChanged()

        AdMobHelper.initAdUnit(activity, adView, R.string.adTasksPage)
    }

    override fun onResume() {
        super.onResume()
        SetTitle(R.string.lbl_tasks)

        GetTaskEntries(this).execute(day)
    }

    fun notifyUpdate() {
        GetTaskEntries(this).execute(day)
    }

    override fun onGetTasksSuccess(items: List<TaskEntry>) {

        this.activity.runOnUiThread {
            adapter!!.updateData(items)
        }
    }

    override fun onGetTasksError(message: String) {

        showErrorMessage(message)
    }

    companion object {

        fun newInstance(day: Int): TasksFragment {

            val fragment = TasksFragment()

            fragment.day = day

            return fragment
        }
    }
}
