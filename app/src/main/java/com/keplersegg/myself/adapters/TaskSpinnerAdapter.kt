package com.keplersegg.myself.adapters

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import com.keplersegg.myself.Room.Entity.Task
import com.keplersegg.myself.activities.MainActivity

class TaskSpinnerAdapter(activity: MainActivity, textViewResourceId: Int,
                         private val values: ArrayList<Task>) : ArrayAdapter<Task>(activity, textViewResourceId, values) {

    override fun getCount(): Int {
        return values.size
    }

    override fun getItem(position: Int): Task? {
        return values[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // This is for the "passive" state of the spinner
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val label = super.getView(position, convertView, parent) as TextView
        label.setTextColor(Color.BLACK)
        label.text = values[position].Label

        return label
    }

    // This is when the "chooser" is popped up
    override fun getDropDownView(position: Int, convertView: View?,
                                 parent: ViewGroup): View {
        val label = super.getDropDownView(position, convertView, parent) as TextView
        label.setTextColor(Color.BLACK)
        label.text = values[position].Label

        return label
    }
}