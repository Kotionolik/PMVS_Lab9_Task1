package com.fedar.weatherapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForecastResponse(
    val list: List<ForecastItem>,
    val city: City
)

@Serializable
data class ForecastItem(
    val dt: Long,
    val main: MainForecast,
    val weather: List<WeatherInfo>,
    @SerialName("dt_txt") val dtTxt: String
)

@Serializable
data class MainForecast(
    val temp: Double,
    @SerialName("feels_like") val feelsLike: Double,
    val humidity: Int
)

@Serializable
data class City(
    val name: String
)