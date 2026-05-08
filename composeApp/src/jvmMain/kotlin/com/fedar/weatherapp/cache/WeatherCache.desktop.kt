package com.fedar.weatherapp.cache

import com.fedar.weatherapp.model.ForecastResponse
import com.fedar.weatherapp.model.WeatherResponse
import kotlinx.serialization.json.Json
import java.io.File

actual class WeatherCache : WeatherCacheApi {
    private val cacheDir = File(System.getProperty("user.home"), ".weatherapp_cache")
    private val json = Json { ignoreUnknownKeys = true }

    init {
        if (!cacheDir.exists()) cacheDir.mkdirs()
    }

    private fun currentWeatherFile(city: String) = File(cacheDir, "weather_${city.lowercase()}.json")
    private fun forecastFile(city: String) = File(cacheDir, "forecast_${city.lowercase()}.json")

    actual override suspend fun saveCurrentWeather(city: String, data: WeatherResponse) {
        currentWeatherFile(city).writeText(json.encodeToString(data))
    }

    actual override suspend fun getCachedCurrentWeather(city: String): WeatherResponse? {
        val file = currentWeatherFile(city)
        return if (file.exists()) {
            try {
                json.decodeFromString<WeatherResponse>(file.readText())
            } catch (e: Exception) {
                null
            }
        } else null
    }

    actual override suspend fun saveForecast(city: String, data: ForecastResponse) {
        forecastFile(city).writeText(json.encodeToString(data))
    }

    actual override suspend fun getCachedForecast(city: String): ForecastResponse? {
        val file = forecastFile(city)
        return if (file.exists()) {
            try {
                json.decodeFromString<ForecastResponse>(file.readText())
            } catch (e: Exception) {
                null
            }
        } else null
    }
}