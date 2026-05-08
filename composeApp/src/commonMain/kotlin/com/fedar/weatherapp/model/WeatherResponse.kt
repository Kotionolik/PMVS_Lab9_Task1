package com.fedar.weatherapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    val name: String,
    val main: MainData,
    val weather: List<WeatherInfo>,
    val wind: Wind,
    val sys: Sys,
    val dt: Long
)

@Serializable
data class MainData(
    val temp: Double,
    @SerialName("feels_like") val feelsLike: Double,
    val humidity: Int,
    val pressure: Int
)

@Serializable
data class WeatherInfo(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

@Serializable
data class Wind(
    val speed: Double
)

@Serializable
data class Sys(
    val sunrise: Long,
    val sunset: Long
)