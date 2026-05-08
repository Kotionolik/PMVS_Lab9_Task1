package com.fedar.weatherapp.repository

import com.fedar.weatherapp.cache.WeatherCacheApi
import com.fedar.weatherapp.model.ForecastResponse
import com.fedar.weatherapp.model.WeatherResponse
import com.fedar.weatherapp.network.WeatherApi
import com.fedar.weatherapp.network.WeatherApiClient

class WeatherRepository(
    private val apiClient: WeatherApi = WeatherApiClient(),
    private val cache: WeatherCacheApi
) {
    suspend fun getCurrentWeather(city: String): WeatherResponse? {
        return try {
            val data = apiClient.getCurrentWeather(city)
            cache.saveCurrentWeather(city, data)
            data
        } catch (e: Exception) {
            println("⚠️ Network error for $city: ${e.message}")
            cache.getCachedCurrentWeather(city)
        }
    }

    suspend fun getForecast(city: String): ForecastResponse? {
        return try {
            val data = apiClient.getForecast(city)
            cache.saveForecast(city, data)
            data
        } catch (e: Exception) {
            println("⚠️ Network error for $city: ${e.message}")
            cache.getCachedForecast(city)
        }
    }
}