package com.keplersegg.myself.helper

import java.util.*
import java.util.concurrent.TimeUnit


object Utils {

    fun getToday(): Int {

        return getDay(Calendar.getInstance())
    }

    fun getDay(calendar: Calendar): Int {

        val calZero = getCalZero()

        val msDiff = calendar.timeInMillis - calZero.timeInMillis
        return TimeUnit.MILLISECONDS.toDays(msDiff).toInt() + 1
    }

    private fun getDateWithoutTime(): Calendar {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar
    }

    fun getDayBack(daysBack: Int): Long {

        val today = getDateWithoutTime()

        today.add(Calendar.DATE, daysBack)

        return today.timeInMillis
    }

    fun getDate(day: Int): Calendar {

        val cal = getCalZero()

        cal.add(Calendar.DATE, day)

        return cal
    }

    private fun getCalZero(): Calendar {

        val calZero = Calendar.getInstance()
        calZero.set(2018, Calendar.JANUARY, 1)
        return calZero
    }

    fun toInt(value: String?): Int? {

        if (value == null) return null
        return value.toIntOrNull()
    }
}