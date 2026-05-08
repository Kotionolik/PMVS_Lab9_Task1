package com.fedar.weatherapp.cache

import com.fedar.weatherapp.model.ForecastResponse
import com.fedar.weatherapp.model.WeatherResponse
import kotlinx.browser.localStorage
import kotlinx.serialization.json.Json

actual class WeatherCache : WeatherCacheApi {
    private val json = Json { ignoreUnknownKeys = true }

    private fun keyWeather(city: String) = "weather_$city"
    private fun keyForecast(city: String) = "forecast_$city"

    actual override suspend fun saveCurrentWeather(city: String, data: WeatherResponse) {
        localStorage.setItem(keyWeather(city), json.encodeToString(data))
    }

    actual override suspend fun getCachedCurrentWeather(city: String): WeatherResponse? {
        return localStorage.getItem(keyWeather(city))?.let {
            try { json.decodeFromString(it) } catch (e: Exception) { null }
        }
    }

    actual override suspend fun saveForecast(city: String, data: ForecastResponse) {
        localStorage.setItem(keyForecast(city), json.encodeToString(data))
    }

    actual override suspend fun getCachedForecast(city: String): ForecastResponse? {
        return localStorage.getItem(keyForecast(city))?.let {
            try { json.decodeFromString(it) } catch (e: Exception) { null }
        }
    }
}