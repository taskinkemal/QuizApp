package com.keplersegg.myself.fragments


import android.os.Bundle
import android.view.*
import androidx.viewpager.widget.ViewPager
import com.getbase.floatingactionbutton.FloatingActionButton
import com.getbase.floatingactionbutton.FloatingActionsMenu

import com.keplersegg.myself.R
import com.keplersegg.myself.adapters.TaskPagerAdapter
import java.text.DateFormatSymbols
import java.util.*
import com.google.android.material.tabs.TabLayout
import com.keplersegg.myself.async.SyncTasks
import com.keplersegg.myself.helper.AutoTaskType
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class TasksPagerFragment : MasterFragment() {

    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var fabAddAuto: FloatingActionButton
    private lateinit var famAddMenu: FloatingActionsMenu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.layout = R.layout.fragment_tasks_pager
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        rootView = super.onCreateView(inflater, container, savedInstanceState)

        viewPager = rootView!!.findViewById(R.id.viewPager)
        tabLayout = rootView!!.findViewById(R.id.tabLayout)
        fabAdd = rootView!!.findViewById(R.id.fabAdd)
        fabAddAuto = rootView!!.findViewById(R.id.fabAddAuto)
        famAddMenu = rootView!!.findViewById(R.id.famAddMenu)

        val adapter = TaskPagerAdapter(childFragmentManager) // activity!!.supportFragmentManager)
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        createAdapter()

        return rootView
    }

    private fun createAdapter() {

        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener
        {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        fabAdd.setOnClickListener {
            famAddMenu.collapse()
            activity.NavigateFragment(true, AddTaskFragment.newInstance(0))
        }
        fabAddAuto.setOnClickListener {
            famAddMenu.collapse()
            activity.NavigateFragment(true, AutoTaskSelectorFragment.newInstance())
        }
    }

    private fun dateToString(days: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -days)
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        return DateFormatSymbols().shortWeekdays[dayOfWeek]
    }

    override fun onResume() {
        super.onResume()
        SetTitle(R.string.lbl_tasks)

        tabLayout.removeAllTabs()
        for (i: Int in 0 .. TaskPagerAdapter.NumOfTabs - 3) {

            val title = dateToString(TaskPagerAdapter.NumOfTabs - 1 - i)
            tabLayout.addTab(tabLayout.newTab().setText(title))
        }
        tabLayout.addTab(tabLayout.newTab().setText("Yesterday"))
        tabLayout.addTab(tabLayout.newTab().setText("Today"))

        viewPager.adapter!!.notifyDataSetChanged()
        viewPager.currentItem = TaskPagerAdapter.NumOfTabs - 1

        doAsync {
            val automatedTasks = activity.master.AppDB()
                    .taskDao()
                    .all
                    .filter { t -> t.AutomationType != null && SyncTasks.PermissionChecker.isPermissionMissing(activity, t.AutomationType) }
                    .map { t -> AutoTaskType.valueOf(t.AutomationType!!)!! }
                    .distinct()

            uiThread {
                if (automatedTasks.isNotEmpty()) {
                    activity.master.requestPermissions(automatedTasks)
                }
                else if (!activity.app.dataStore.getTutorialDone()) {

                    val tutorial = DialogAppTutorial()
                    tutorial.activity = activity
                    tutorial.show(fragmentManager!!, "tutorial")
                }
            }
        }
    }

    companion object {

        fun newInstance(): TasksPagerFragment {
            return TasksPagerFragment()
        }
    }
}
