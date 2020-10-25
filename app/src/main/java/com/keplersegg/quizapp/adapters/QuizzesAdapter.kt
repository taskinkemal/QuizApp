package com.keplersegg.quizapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

import com.keplersegg.quizapp.activities.MainActivity
import com.keplersegg.quizapp.fragments.AddTaskFragment
import com.keplersegg.quizapp.helper.AutoTaskType
import com.keplersegg.quizapp.helper.HttpClient
import com.keplersegg.quizapp.helper.ServiceMethods
import com.keplersegg.quizapp.R
import com.keplersegg.quizapp.Room.Entity.Entry
import com.keplersegg.quizapp.Room.Entity.TaskEntry
import com.keplersegg.quizapp.async.GetQuizzes
import com.keplersegg.quizapp.fragments.AppUsageFragment
import com.keplersegg.quizapp.models.TaskGeneralStats
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*
import kotlin.collections.ArrayList



class TasksAdapter(private val activity: MainActivity, private val day: Int) : RecyclerView.Adapter<TasksAdapter.DataObjectHolder>() {

    private val items: ArrayList<TaskEntry> = ArrayList()

    inner class DataObjectHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val lblLabel: TextView = itemView.findViewById(R.id.lblLabel)
        val imgDone: ImageButton? = itemView.findViewById(R.id.imgDone)
        val imgPlus: ImageButton? = itemView.findViewById(R.id.imgPlus)
        val imgMinus: ImageButton? = itemView.findViewById(R.id.imgMinus)
        val txtValue: TextView? = itemView.findViewById(R.id.txtValue)
        val txtUnit: TextView? = itemView.findViewById(R.id.txtUnit)
        val imgTaskType: ImageView? = itemView.findViewById(R.id.imgTaskType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataObjectHolder {

        val layout = when (viewType) {
            0 -> R.layout.list_item_task_quiz
            1 -> R.layout.list_item_task_numeric
            else -> R.layout.list_item_task_minutes
        }
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)

        return DataObjectHolder(itemView = view)
    }

    init {

        this.setHasStableIds(true)
    }

    override fun onBindViewHolder(holder: DataObjectHolder, position: Int) {

        // object item based on the position
        val item = if (items.size > position) items[position] else null

        if (item != null) {

            var entry: Entry? = item.entry
            if (entry == null) {

                entry = Entry()
                entry.TaskId = item.task!!.Id
                entry.Day = day
                entry.Value = 0
                entry.ModificationDate = Date(System.currentTimeMillis())
                item.entry = entry

                doAsync {
                    activity.AppDB().entryDao().insert(item.entry!!)
                    if (HttpClient.hasInternetAccess(activity)) {
                        uploadEntry(item.entry!!)
                    }
                uiThread { updateUi(holder, item) }
                }
            } else {
                updateUi(holder, item)
            }
        }

        holder.itemView.setOnLongClickListener(View.OnLongClickListener {
            if (item != null) {

                activity.NavigateFragment(true, AddTaskFragment.newInstance(item.task!!.Id))
                return@OnLongClickListener true
            }
            false
        })

        when {
            holder.itemViewType == 0 -> holder.imgDone!!.setOnClickListener {
                val entry = item!!.entry
                entry!!.Value = if (entry.Value == 0) 1 else 0
                entry.ModificationDate = Date(System.currentTimeMillis())
                setTint(holder.imgDone, entry.Value == 1)

                doAsync {
                    activity.AppDB().entryDao().update(item.entry!!)
                    if (HttpClient.hasInternetAccess(activity)) {
                        uploadEntry(item.entry!!)
                    }
                }
            }
            holder.itemViewType == 1 -> {

                holder.imgPlus!!.setOnClickListener {
                    val entry = item!!.entry
                    entry!!.Value++
                    entry.ModificationDate = Date(System.currentTimeMillis())

                    holder.txtValue!!.text = item.entry!!.Value.toString()

                    doAsync {
                        activity.AppDB().entryDao().update(item.entry!!)
                        if (HttpClient.hasInternetAccess(activity)) {
                            uploadEntry(item.entry!!)
                        }
                    }
                }

                holder.imgMinus!!.setOnClickListener {
                    val entry = item!!.entry
                    if (entry!!.Value > 0)
                        entry.Value--
                    entry.ModificationDate = Date(System.currentTimeMillis())

                    holder.txtValue!!.text = item.entry!!.Value.toString()

                    doAsync {
                        activity.AppDB().entryDao().update(item.entry!!)
                        if (HttpClient.hasInternetAccess(activity)) {
                            uploadEntry(item.entry!!)
                        }
                    }
                }
            }
            else -> //TODO: holder.txtValue!!.text = (item!!.entry!!.Value / 60).toString()
                holder.txtValue!!.text = (item!!.entry!!.Value).toString()
        }
    }

    private fun updateUi(holder: DataObjectHolder, item: TaskEntry) {

        holder.lblLabel.text = item.task!!.Label
        if (holder.itemViewType == 0) {
            setTint(holder.imgDone!!, item.entry!!.Value == 1)
        }
        else {
            holder.txtValue!!.text = item.entry!!.Value.toString()
            if (holder.txtUnit != null)
                holder.txtUnit.text = item.task!!.Unit
        }
        if (item.task!!.AutomationType == null || item.task!!.AutomationType == 0) {

            holder.imgTaskType!!.visibility = View.INVISIBLE

        } else {

            holder.imgTaskType!!.visibility = View.VISIBLE
            if (item.task!!.AutomationType == AutoTaskType.AppUsage.typeId) {
                holder.imgTaskType.setImageDrawable(AppUsageFragment.getApplicationIcon(activity, item.task!!.AutomationVar!!))
            } else {
                holder.imgTaskType.setImageResource(getTaskTypeImageResource(item.task!!.AutomationType!!))
            }
        }
    }

    private fun getTaskTypeImageResource(automationType: Int): Int {

        return TaskGeneralStats.taskTypeImageResourceId(automationType)!!
    }

    private fun setTint(imgDone: ImageButton, isChecked: Boolean) {

        val color = if (isChecked) R.color.colorAccent else android.R.color.darker_gray
        imgDone.setColorFilter(ContextCompat.getColor(activity, color),
                android.graphics.PorterDuff.Mode.SRC_IN)
    }

    override fun getItemId(position: Int): Long {

        return items[position].task!!.Id.toLong()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (items.count() > position) {
            if (items[position].task!!.AutomationType == AutoTaskType.CallDuration.typeId ||
                    items[position].task!!.AutomationType == AutoTaskType.AppUsage.typeId)
                2 else
            items[position].task!!.DataType
        }
        else 0
    }

    private fun uploadEntry(entry: Entry) {

        val response = ServiceMethods.uploadEntry(activity, entry)

        GetQuizzes(this.activity).upsertBadge(response)
        activity.showNewBadgeDialog()
    }

    fun updateData(list: List<TaskEntry>) {

        this.activity.runOnUiThread {
            items.clear()
            items.addAll(list)
            notifyDataSetChanged()
        }
    }
}