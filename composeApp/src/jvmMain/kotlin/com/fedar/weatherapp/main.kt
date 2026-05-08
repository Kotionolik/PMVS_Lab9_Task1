package com.fedar.weatherapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.fedar.weatherapp.cache.WeatherCache
import com.fedar.weatherapp.model.ForecastItem
import com.fedar.weatherapp.model.ForecastResponse
import com.fedar.weatherapp.model.WeatherResponse
import com.fedar.weatherapp.repository.WeatherRepository
import com.fedar.weatherapp.util.formatDate
import kotlinx.coroutines.launch
//import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    var cityInput by remember { mutableStateOf("") }
    var currentWeather by remember { mutableStateOf<WeatherResponse?>(null) }
    var forecast by remember { mutableStateOf<ForecastResponse?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val repository = remember { WeatherRepository(cache = WeatherCache()) }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Weather App",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = cityInput,
                    onValueChange = { cityInput = it },
                    label = { Text("Enter city name") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (cityInput.isNotBlank()) {
                            scope.launch {
                                isLoading = true
                                errorMessage = null
                                try {
                                    val weather = repository.getCurrentWeather(cityInput.trim())
                                    val forecastData = repository.getForecast(cityInput.trim())
                                    currentWeather = weather
                                    forecast = forecastData
                                    if (weather == null) errorMessage = "City not found or no network."
                                } catch (e: Exception) {
                                    errorMessage = "Error: ${e.message}"
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                    },
                    enabled = cityInput.isNotBlank() && !isLoading,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Search")
                }

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }

                errorMessage?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(8.dp))
                }

                Spacer(Modifier.height(16.dp))

                currentWeather?.let { weather ->
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        CurrentWeatherView(weather)
                    }

                    Spacer(Modifier.height(24.dp))

                    Text(
                        text = "5-Day Forecast",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )

                    val dailyItems = forecast?.list?.filterIndexed { index, _ ->
                        index % 8 == 0
                    }?.take(5) ?: emptyList()

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(dailyItems) { item ->
                            ElevatedCard(
                                modifier = Modifier
                                    .width(150.dp)
                                    .padding(4.dp),
                                shape = MaterialTheme.shapes.medium,
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                ForecastItemView(item)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CurrentWeatherView(weather: WeatherResponse) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = weather.name,
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "${weather.main.temp}°C",
            fontSize = 48.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Text(
            text = weather.weather.firstOrNull()?.description?.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            } ?: "",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WeatherProperty(label = "Feels like", value = "${weather.main.feelsLike}°C")
            WeatherProperty(label = "Humidity", value = "${weather.main.humidity}%")
            WeatherProperty(label = "Wind", value = "${weather.wind.speed} m/s")
        }
    }
}

@Composable
fun WeatherProperty(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun ForecastItemView(item: ForecastItem) {
    val dateFormat = remember { formatDate(item.dt) }
    Column(
        modifier = Modifier.padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = dateFormat.format(Date(item.dt * 1000)),
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center
        )
        Text(
            text = "${item.main.temp}°C",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        Text(
            text = item.weather.firstOrNull()?.description ?: "",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}

fun main() {
    application {
        val windowState = rememberWindowState(width = 800.dp, height = 700.dp)
        Window(
            onCloseRequest = ::exitApplication,
            title = "Weather App",
            state = windowState
        ) {
            App()
        }
    }
}