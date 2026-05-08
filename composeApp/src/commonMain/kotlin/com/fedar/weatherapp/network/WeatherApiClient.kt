package com.fedar.weatherapp.network

import com.fedar.weatherapp.Config
import com.fedar.weatherapp.model.ForecastResponse
import com.fedar.weatherapp.model.WeatherResponse
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class WeatherApiClient : WeatherApi {
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    override suspend fun getCurrentWeather(city: String): WeatherResponse {
        return httpClient.get("https://api.openweathermap.org/data/2.5/weather") {
            parameter("q", city)
            parameter("appid", Config.API_KEY)
            parameter("units", "metric")
        }.body()
    }

    override suspend fun getForecast(city: String): ForecastResponse {
        return httpClient.get("https://api.openweathermap.org/data/2.5/forecast") {
            parameter("q", city)
            parameter("appid", Config.API_KEY)
            parameter("units", "metric")
        }.body()
    }
}