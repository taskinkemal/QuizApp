package com.keplersegg.myself.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.LayoutInflater
import com.keplersegg.myself.R
import android.widget.RelativeLayout

class PeekStatView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleRes: Int = 0
) : RelativeLayout(context, attrs, defStyleRes) {

    private var values: MutableList<Int>? = null
    private val paint = Paint()

    init {
        LayoutInflater.from(context)
                .inflate(R.layout.component_peek_stat, this, true)

        paint.isDither = true
        paint.isAntiAlias = true
        paint.strokeWidth = 2.0f
    }

    fun setValues(values: MutableList<Int>) {

        this.values = values
        this.invalidate()
        this.refreshDrawableState()
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)

        if (canvas == null) return
        if (values == null) return

        val valuesSize = values!!.size

        if (valuesSize < 5) {

            for (i in 0..5-valuesSize) {

                values!!.add(0, 0)
            }
        }

        val yMax = values!!.max()!!
        val yMin = values!!.min()!!
        val radius = 10.0f

        val innerWidth = width - (radius * 2)
        val innerHeight = height - (radius * 2)

        val w = innerWidth / 4

        for (i in 0..3) {

            val xStart = i * w + radius
            val xEnd = (i + 1) * w + radius
            val yStart = getYCoordinate(values!![i], yMin, yMax, innerHeight) + radius
            val yEnd = getYCoordinate(values!![i + 1], yMin, yMax, innerHeight) + radius

            canvas.drawLine(xStart, yStart, xEnd, yEnd, paint)
            if (i == 0)
                canvas.drawCircle(xStart, yStart, radius, paint)
            canvas.drawCircle(xEnd, yEnd, radius, paint)
        }
    }

    private fun getYCoordinate(y: Int, yMin: Int, yMax: Int, height: Float) : Float {

        if (yMax == yMin) return height / 2

        return (height * (yMax - y)) / (yMax - yMin)
    }
}