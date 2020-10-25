package com.keplersegg.myself.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.keplersegg.myself.R
import kotlinx.android.synthetic.main.component_line_stat.view.*
import android.graphics.PorterDuff



class LineStatView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleRes: Int = 0
) : CardView(context, attrs, defStyleRes) {

    internal var value: Int = 0
    private var label: String? = ""
    private var unit: String? = ""
    private var color: Int = 0
    private var trend: Int? = null

    init {
        LayoutInflater.from(context)
                .inflate(R.layout.component_line_stat, this, true)

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it,
                    R.styleable.LineStatView, 0, 0)

            value = typedArray.getInteger(R.styleable.LineStatView_value, 0)
            trend = if (typedArray.hasValue(R.styleable.LineStatView_trend)) typedArray.getInteger(R.styleable.LineStatView_trend, 0) else null
            val attrLabel = typedArray.getString(R.styleable.LineStatView_text)
            label = if (attrLabel != null) attrLabel else ""
            val attrUnit = typedArray.getString(R.styleable.LineStatView_unit)
            unit = if (attrUnit != null) attrUnit else resources.getString(R.string.time_plural)
            color = typedArray.getColor(R.styleable.LineStatView_valueColor, ContextCompat.getColor(context, R.color.colorBackground))

            lblLabel.text = label
            lblValue.text = value.toString()
            lblUnit.text = unit
            val drawable = lblValue.background
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)

            typedArray.recycle()
        }

        this.background =  ContextCompat.getDrawable(context, android.R.color.transparent)
    }

    fun setValue(value: Int) {
        this.value = value
        this.lblValue.text = value.toString()
        this.lblValue.invalidate()
    }

    fun setUnit(unit: String) {
        this.unit = unit
        this.lblUnit.text = unit
        this.lblUnit.invalidate()
    }

    //TODO:
    fun setTrend(trend: Int) {
        this.trend = trend
    }
}