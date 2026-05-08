package com.fedar.weatherapp.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

actual fun formatDate(epochSeconds: Long): String {
    val format = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
    return format.format(Date(epochSeconds * 1000))
}