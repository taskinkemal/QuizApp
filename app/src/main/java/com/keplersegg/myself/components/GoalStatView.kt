package com.keplersegg.myself.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.keplersegg.myself.R
import kotlinx.android.synthetic.main.component_goal_stat.view.*
import android.graphics.PorterDuff



class GoalStatView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleRes: Int = 0
) : CardView(context, attrs, defStyleRes) {

    private var total: Int = 0
    private var label: String? = ""
    private var color: Int = 0

    init {
        LayoutInflater.from(context)
                .inflate(R.layout.component_goal_stat, this, true)

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it,
                    R.styleable.GoalStatView, 0, 0)

            total = typedArray.getInteger(R.styleable.GoalStatView_total, 0)
            val attrLabel = typedArray.getString(R.styleable.GoalStatView_label)
            label = if (attrLabel != null) attrLabel else ""
            color = typedArray.getColor(R.styleable.GoalStatView_color, ContextCompat.getColor(context, R.color.colorBackground))

            lblLabel.text = label
            lblValue.text = total.toString()
            val drawable = lblValue.getBackground()
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)

            typedArray.recycle()
        }
    }

    fun setTotal(value: Int) {

        total = value
        lblValue.text = value.toString()
        lblValue.invalidate()
    }
}