package com.fedar.weatherapp.cache

import com.fedar.weatherapp.model.ForecastResponse
import com.fedar.weatherapp.model.WeatherResponse

interface WeatherCacheApi {
    suspend fun saveCurrentWeather(city: String, data: WeatherResponse)
    suspend fun getCachedCurrentWeather(city: String): WeatherResponse?
    suspend fun saveForecast(city: String, data: ForecastResponse)
    suspend fun getCachedForecast(city: String): ForecastResponse?
}