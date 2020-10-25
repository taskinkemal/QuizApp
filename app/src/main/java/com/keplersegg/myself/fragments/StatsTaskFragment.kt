package com.keplersegg.myself.fragments


import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import com.github.mikephil.charting.data.Entry

import com.keplersegg.myself.R
import com.keplersegg.myself.components.DayStatView
import com.keplersegg.myself.helper.AutoTaskType
import com.keplersegg.myself.helper.ChartsHelper
import kotlinx.android.synthetic.main.fragment_stats_task.*
import com.keplersegg.myself.helper.Utils
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import kotlin.collections.ArrayList


class StatsTaskFragment : MasterFragment() {

    private var TaskId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.layout = R.layout.fragment_stats_task
    }

    override fun onResume() {
        super.onResume()

        doAsync {

            val today = Utils.getToday()
            val allEntries = activity.AppDB().entryDao().all.filter { e -> e.TaskId == TaskId && e.Day > today - 90 }.sortedBy { e -> e.Day }
            val task = activity.AppDB().taskDao()[TaskId]
            val entries = getEntries(allEntries)

            if (entries.size > 1)
            {
                ChartsHelper.initEntriesChart(graphEntriesPerDay, entries)
            }

            val unit = if (task.Unit.isNotBlank()) task.Unit else resources.getString(R.string.time_plural)
            val totalLastWeek = allEntries.filter { e -> e.Day >= today - 7 }.sumBy { e -> e.Value }
            val totalPreviousWeek = allEntries.filter { e -> e.Day >= today - 14 && e.Day < today - 7 }.sumBy { e -> e.Value }
            val totalLastMonth = allEntries.filter { e -> e.Day >= today - 30 }.sumBy { e -> e.Value }
            val totalPreviousMonth = allEntries.filter { e -> e.Day >= today - 60 && e.Day < today - 30 }.sumBy { e -> e.Value }
            val currentStreak = getStreak(allEntries, today)
            val highestStreak = getHighestStreak(allEntries)
            val highestDays = allEntries.sortedByDescending { e -> e.Value }
            val allGoals = activity.AppDB().goalDao().all.filter { g -> g.TaskId == TaskId }
            val trendLastWeek =
                    if (totalLastWeek > totalPreviousWeek) 1 else
                        if (totalLastWeek == totalPreviousWeek) 0 else
                            if (totalLastWeek < totalPreviousWeek) -1 else Int.MIN_VALUE
            val trendLastMonth =
                    if (totalLastMonth > totalPreviousMonth) 1 else
                        if (totalLastMonth == totalPreviousMonth) 0 else
                            if (totalLastMonth < totalPreviousMonth) -1 else Int.MIN_VALUE

            val successful = allGoals.count { g -> g.AchievementStatus == 1 }
            val failed = allGoals.count { g -> g.AchievementStatus == 2 }
            val inProgress = allGoals.count { g -> g.AchievementStatus == 0 }

            //TODO: highest days

            uiThread {

                if (totalLastWeek > 600 && task.AutomationType == AutoTaskType.AppUsage.typeId) {
                    lsvLastWeek.setValue(totalLastWeek / 60)
                    lsvLastWeek.setUnit(resources.getString(R.string.lbl_hours))
                }
                else {
                    lsvLastWeek.setValue(totalLastWeek)
                    lsvLastWeek.setUnit(unit)
                }
                if (totalLastMonth > 600 && task.AutomationType == AutoTaskType.AppUsage.typeId) {
                    lsvLastMonth.setValue(totalLastMonth / 60)
                    lsvLastMonth.setUnit(resources.getString(R.string.lbl_hours))
                }
                else {
                    lsvLastMonth.setValue(totalLastMonth)
                    lsvLastMonth.setUnit(unit)
                }
                lsvLastWeek.setTrend(trendLastWeek)
                lsvLastMonth.setTrend(trendLastMonth)
                lsvCurrentStreak.setValue(currentStreak)
                lsvHighestStreak.setValue(highestStreak)
                gsvSuccessful.setTotal(successful)
                gsvFailed.setTotal(failed)
                gsvInProgress.setTotal(inProgress)

                val highestDayMax = if (highestDays.size >= 3) 2 else highestDays.size - 1
                for (i in 0..highestDayMax) {
                    val dayStatView = DayStatView(activity)
                    val params : ViewGroup.LayoutParams = ViewGroup.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, // This will define text view width
                            LinearLayout.LayoutParams.WRAP_CONTENT // This will define text view height
                    )
                    dayStatView.layoutParams = params
                    dayStatView.setBackgroundColor(Color.TRANSPARENT)
                    dayStatView.setCardBackgroundColor(null)
                    dayStatView.setData(highestDays[i].Day, highestDays[i].Value, task.Unit)
                    lytHighestDays.addView(dayStatView)
                }
            }
        }
    }

    private fun getHighestStreak(allEntries: List<com.keplersegg.myself.Room.Entity.Entry>) : Int {

        var streak = 0
        var current = 0

        for (i in allEntries.size-1 downTo 0) {

            if (allEntries[i].Value == 0) {

                if (current > streak) streak = current
                current = 0
                continue
            }

            if (i > 0 && allEntries[i].Day == allEntries[i-1].Day + 1) {
                current++
            } else {

                current++
                if (current > streak) streak = current
                current = 0
                continue
            }
        }

        if (current > streak) streak = current
        return streak
    }

    private fun getStreak(allEntries: List<com.keplersegg.myself.Room.Entity.Entry>, start: Int) : Int {

        var streak = 0

        var day = start

        while (true) {

            val entry = allEntries.find { e -> e.Day == day }

            if (entry == null || entry.Value == 0) break
            day--
            streak++
        }

        return streak
    }

    private fun getEntries(allEntries: List<com.keplersegg.myself.Room.Entity.Entry>) : ArrayList<Entry> {

        val entries = ArrayList<Entry>()
        val today = Utils.getToday()

        if (allEntries.size > 1)
        {
            val minDay = allEntries.first().Day
            val maxDay = today

            var index = 0
            for (day: Int in minDay..maxDay) {

                var value = 0

                if (allEntries[index].Day == day) {

                    value = allEntries[index].Value
                    index++
                }

                entries.add(Entry(day.toFloat(), value.toFloat()))
            }
        }

        return entries
    }

    companion object {

        fun newInstance(taskId: Int): StatsTaskFragment {
            val fragment = StatsTaskFragment()
            fragment.TaskId = taskId
            return fragment
        }
    }
}
