package com.fedar.weatherapp.network

import com.fedar.weatherapp.model.ForecastResponse
import com.fedar.weatherapp.model.WeatherResponse

interface WeatherApi {
    suspend fun getCurrentWeather(city: String): WeatherResponse
    suspend fun getForecast(city: String): ForecastResponse
}