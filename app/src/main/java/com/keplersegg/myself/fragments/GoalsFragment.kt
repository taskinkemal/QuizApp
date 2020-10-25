package com.keplersegg.myself.fragments


import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.keplersegg.myself.MySelfApplication

import com.keplersegg.myself.R
import com.keplersegg.myself.Room.AppDatabase
import com.keplersegg.myself.Room.Entity.Goal
import com.keplersegg.myself.adapters.GoalsAdapter
import com.keplersegg.myself.async.SyncGoals
import com.keplersegg.myself.interfaces.ISyncGoalsHost
import kotlinx.android.synthetic.main.fragment_goals.*

class GoalsFragment : MasterFragment(), ISyncGoalsHost {

    private lateinit var adapter: GoalsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.layout = R.layout.fragment_goals

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = GoalsAdapter(activity)
        rcylGoals!!.adapter = adapter
        rcylGoals.layoutManager = LinearLayoutManager(activity)

        fabAdd!!.setOnClickListener { activity.NavigateFragment(true, AddGoalFragment.newInstance(-1, -1)) }
    }

    override fun onResume() {
        super.onResume()
        SetTitle(R.string.lbl_goals)

        SyncGoals(this).execute()
    }

    override fun onSyncGoalsSuccess(list: List<Goal>) {

        adapter.updateData(list)
        activity.showNewBadgeDialog()
    }

    override fun AppDB(): AppDatabase {
        return activity.AppDB()
    }

    override fun GetApplication(): MySelfApplication {
        return activity.GetApplication()
    }

    companion object {

        fun newInstance(): GoalsFragment {

            return GoalsFragment()
        }
    }
}
