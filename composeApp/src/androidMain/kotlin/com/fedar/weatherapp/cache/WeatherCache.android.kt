package com.fedar.weatherapp.cache

import android.content.Context
import com.fedar.weatherapp.model.ForecastResponse
import com.fedar.weatherapp.model.WeatherResponse
import kotlinx.serialization.json.Json

actual class WeatherCache(private val context: Context) : WeatherCacheApi {

    private val prefs = context.getSharedPreferences("weather_cache", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    private fun keyWeather(city: String) = "weather_$city"
    private fun keyForecast(city: String) = "forecast_$city"

    actual override suspend fun saveCurrentWeather(city: String, data: WeatherResponse) {
        prefs.edit().putString(keyWeather(city), json.encodeToString(data)).apply()
    }

    actual override suspend fun getCachedCurrentWeather(city: String): WeatherResponse? {
        return prefs.getString(keyWeather(city), null)?.let {
            try { json.decodeFromString(it) } catch (e: Exception) { null }
        }
    }

    actual override suspend fun saveForecast(city: String, data: ForecastResponse) {
        prefs.edit().putString(keyForecast(city), json.encodeToString(data)).apply()
    }

    actual override suspend fun getCachedForecast(city: String): ForecastResponse? {
        return prefs.getString(keyForecast(city), null)?.let {
            try { json.decodeFromString(it) } catch (e: Exception) { null }
        }
    }
}