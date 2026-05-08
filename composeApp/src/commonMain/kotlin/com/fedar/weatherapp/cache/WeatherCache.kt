package com.fedar.weatherapp.cache

import com.fedar.weatherapp.model.ForecastResponse
import com.fedar.weatherapp.model.WeatherResponse

expect class WeatherCache : WeatherCacheApi {
    override suspend fun saveCurrentWeather(city: String, data: WeatherResponse)
    override suspend fun getCachedCurrentWeather(city: String): WeatherResponse?
    override suspend fun saveForecast(city: String, data: ForecastResponse)
    override suspend fun getCachedForecast(city: String): ForecastResponse?
}