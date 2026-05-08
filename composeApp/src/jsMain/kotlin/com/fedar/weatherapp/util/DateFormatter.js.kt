package com.fedar.weatherapp.util

actual fun formatDate(epochSeconds: Long): String {
    val options = js("{ weekday: 'short', month: 'short', day: 'numeric' }")
    val date = js("new Date(epochSeconds * 1000)")
    return date.toLocaleDateString(js("undefined"), options) as String
}