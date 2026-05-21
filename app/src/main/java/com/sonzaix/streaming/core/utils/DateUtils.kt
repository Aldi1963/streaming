package com.sonzaix.streaming.core.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun formatTimestamp(millis: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        return sdf.format(Date(millis))
    }

    fun formatDuration(ms: Long): String {
        val totalSec = ms / 1000
        val min = totalSec / 60
        val sec = totalSec % 60
        return "%02d:%02d".format(min, sec)
    }
}
