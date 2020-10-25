package com.keplersegg.myself.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.keplersegg.myself.R
import com.keplersegg.myself.Room.Entity.Goal
import com.keplersegg.myself.activities.MainActivity
import com.keplersegg.myself.fragments.AddGoalFragment
import com.keplersegg.myself.helper.Utils
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class GoalsAdapter(private val activity: MainActivity) : RecyclerView.Adapter<GoalsAdapter.DataObjectHolder>() {

    val items: ArrayList<Goal> = ArrayList()

    inner class DataObjectHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val lblTask: TextView = itemView.findViewById(R.id.lblTask)
        val lblTarget: TextView = itemView.findViewById(R.id.lblTarget)
        val lblEndDate: TextView = itemView.findViewById(R.id.lblEndDate)
        val lblCurrentValue: TextView = itemView.findViewById(R.id.lblCurrentValue)
        val imgStatus: ImageView = itemView.findViewById(R.id.imgStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataObjectHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_goal_card, parent, false)

        return DataObjectHolder(itemView = view)
    }

    init {

        this.setHasStableIds(true)
    }

    override fun onBindViewHolder(holder: DataObjectHolder, position: Int) {

        // object item based on the position
        val item = if (items.size > position) items[position] else null

        holder.itemView.setOnLongClickListener(View.OnLongClickListener {
            if (item != null) {

                activity.NavigateFragment(true, AddGoalFragment.newInstance(item.TaskId, item.Id))
                return@OnLongClickListener true
            }
            false
        })

        updateUi(holder, item!!)
    }

    private fun updateUi(holder: DataObjectHolder, item: Goal) {

        holder.lblCurrentValue.text = item.CurrentValue.toString()
        holder.lblEndDate.text = getEndDateText(item.EndDay)
        initStatusImage(holder.imgStatus, item.AchievementStatus)

        doAsync {
            val task = activity.AppDB().taskDao().get(item.TaskId)

            uiThread {
                holder.lblTask.text = task.Label
                holder.lblTarget.text = getTargetText(item.Target, task.Unit, item.MinMax)
            }
        }
    }

    private fun getTargetText(target: Int, unit: String, minMax: Int) : String {

        val localUnit = if (unit == "") activity.getString(R.string.lbl_items) else unit
        val result = target.toString() + " " + localUnit
        if (minMax == 2) {
            return result + " " + activity.getString(R.string.minMaxOrMore)
        }
        else if (minMax == 3) {
            return result + " " + activity.getString(R.string.minMaxOrLess)
        }
        else {
            return result
        }
    }

    private fun getEndDateText(day: Int) : String {

        val cal = Utils.getDate(day)
        val today = Calendar.getInstance()

        val msDiff = cal.timeInMillis - today.timeInMillis
        val daysLeft = TimeUnit.MILLISECONDS.toDays(msDiff).toInt()
        val sDaysLeft = if (daysLeft >= 0) " (" + daysLeft.toString() + " " + activity.getString(R.string.lbl_days_remaining) + ")" else ""

        var datePattern = "d MMMM yyyy"
        if (today.get(Calendar.YEAR) == cal.get(Calendar.YEAR)) {

            datePattern = "d MMMM"
        }

        val simpleDateFormat = SimpleDateFormat(datePattern, Locale.getDefault())
        return simpleDateFormat.format(cal.time) + sDaysLeft
    }

    private fun initStatusImage(img: ImageView, status: Int) {

        if (status == 1) {

            img.setColorFilter(ContextCompat.getColor(activity, R.color.colorAccent),
                    android.graphics.PorterDuff.Mode.SRC_IN)
        }
        else if (status == 2) {

            img.setImageResource(R.drawable.ic_baseline_clear_24px)

            img.setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_red_dark),
                    android.graphics.PorterDuff.Mode.SRC_IN)
        }
        else {

            img.setColorFilter(ContextCompat.getColor(activity, android.R.color.darker_gray),
                    android.graphics.PorterDuff.Mode.SRC_IN)
        }
    }

    override fun getItemId(position: Int): Long {

        return items[position].Id.toLong()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateData(list: List<Goal>) {

        this.activity.runOnUiThread {
            items.clear()
            items.addAll(list)
            notifyDataSetChanged()
        }
    }
}