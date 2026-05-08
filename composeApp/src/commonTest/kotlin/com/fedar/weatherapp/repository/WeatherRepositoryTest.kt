package com.fedar.weatherapp.repository

import com.fedar.weatherapp.cache.WeatherCacheApi
import com.fedar.weatherapp.model.*
import com.fedar.weatherapp.network.WeatherApi
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.mock
import dev.mokkery.everySuspend
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class WeatherRepositoryTest {
    private val apiClient = mock<WeatherApi>()
    private val cache = mock<WeatherCacheApi>()
    private val repository = WeatherRepository(apiClient, cache)

    @Test
    fun `getCurrentWeather returns network data and caches it`() = runTest {
        val city = "Minsk"
        val expected = WeatherResponse(city, MainData(15.0, 14.0, 70, 1012), emptyList(), Wind(3.0), Sys(0,0), 0)
        everySuspend { apiClient.getCurrentWeather(city) } returns expected
        everySuspend { cache.saveCurrentWeather(city, expected) } returns Unit

        val result = repository.getCurrentWeather(city)
        assertEquals(expected, result)
        verifySuspend { cache.saveCurrentWeather(city, expected) }
    }

    @Test
    fun `getCurrentWeather returns cached data when network fails`() = runTest {
        val city = "Minsk"
        val cached = WeatherResponse(city, MainData(10.0, 9.0, 80, 1010), emptyList(), Wind(2.0), Sys(0,0), 0)
        everySuspend { apiClient.getCurrentWeather(city) } throws RuntimeException("Network error")
        everySuspend { cache.getCachedCurrentWeather(city) } returns cached

        val result = repository.getCurrentWeather(city)
        assertEquals(cached, result)
    }

    @Test
    fun `getForecast returns network data and caches it`() = runTest {
        val city = "Minsk"
        val expected = ForecastResponse(emptyList(), City(city))
        everySuspend { apiClient.getForecast(city) } returns expected
        everySuspend { cache.saveForecast(city, expected) } returns Unit

        val result = repository.getForecast(city)
        assertEquals(expected, result)
        verifySuspend { cache.saveForecast(city, expected) }
    }

    @Test
    fun `getForecast returns cached data when network fails`() = runTest {
        val city = "Minsk"
        val cached = ForecastResponse(emptyList(), City(city))
        everySuspend { apiClient.getForecast(city) } throws RuntimeException("Network error")
        everySuspend { cache.getCachedForecast(city) } returns cached

        val result = repository.getForecast(city)
        assertEquals(cached, result)
    }
}