package com.keplersegg.myself.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import com.keplersegg.myself.R
import kotlinx.android.synthetic.main.component_day_stat.view.*
import java.util.*
import java.text.SimpleDateFormat


class DayStatView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleRes: Int = 0
) : CardView(context, attrs, defStyleRes) {

    internal var value: Int = 0
    private var day: Int = 0
    private var unit: String? = ""

    init {
        LayoutInflater.from(context)
                .inflate(R.layout.component_day_stat, this, true)
    }

    fun setData(day: Int, value: Int, unit: String) {
        this.day = day
        this.lblDay.text = dateToString(day)
        this.lblDay.invalidate()
        this.value = value
        this.lblValue.text = value.toString()
        this.lblValue.invalidate()
        this.unit = unit
        this.lblUnit.text = unit
        this.lblUnit.invalidate()

        //TODO: partial background
    }

    private fun dateToString(days: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -days)

        val df = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
        return df.format(cal.time)
    }
}