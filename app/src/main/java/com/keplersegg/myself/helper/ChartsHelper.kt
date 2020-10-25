package com.keplersegg.myself.helper

import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import java.text.SimpleDateFormat
import java.util.*

object ChartsHelper {

    fun initEntriesChart(chart: LineChart, entries: ArrayList<Entry>) {

        val dataSet = LineDataSet(entries, "")
        val lineData = LineData(dataSet)
        lineData.setValueFormatter(getIntegerFormatter())
        val xAxis = chart.xAxis
        xAxis.granularity = 1f // minimum axis-step (interval) is 1
        xAxis.valueFormatter = getXAxisFormatter()
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        dataSet.valueTextSize = 16.toFloat()
        chart.xAxis.setDrawGridLines(false)
        chart.axisLeft.setDrawGridLines(false)
        chart.axisRight.setDrawGridLines(false)
        chart.legend.isEnabled = false
        chart.description.isEnabled = false

        chart.data = lineData
        chart.invalidate()
    }

    private fun getXAxisFormatter() : IAxisValueFormatter {

        val simpleDateFormat = SimpleDateFormat("d MMM", Locale.getDefault())

        return IAxisValueFormatter { value, _ -> simpleDateFormat.format(Utils.getDate(value.toInt()).time) }
    }

    private fun getIntegerFormatter() : IValueFormatter {

        return IValueFormatter { value, _, _, _ ->  "" + value.toInt() }
    }
}