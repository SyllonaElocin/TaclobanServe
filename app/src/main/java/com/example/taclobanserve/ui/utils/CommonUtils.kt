package com.example.taclobanserve.ui.utils

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import java.util.Calendar
import com.example.taclobanserve.TaclobanEvent

fun Long.toTimeString(): String {
    val sdf = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
    return sdf.format(Date(this))
}

fun isWeekend(timestamp: Long): Boolean {
    val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
    val day = calendar.get(Calendar.DAY_OF_WEEK)
    return day == Calendar.SATURDAY || day == Calendar.SUNDAY
}

fun calculateServiceHours(event: TaclobanEvent): Double {
    val totalGoalHours = event.hours.toDoubleOrNull() ?: 0.0
    return if (isWeekend(event.startTime)) {
        totalGoalHours / 2.0
    } else {
        totalGoalHours / 5.0
    }
}