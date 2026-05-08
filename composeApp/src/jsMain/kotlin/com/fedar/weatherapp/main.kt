package com.fedar.weatherapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.ComposeViewport
import com.fedar.weatherapp.cache.WeatherCache
import com.fedar.weatherapp.model.ForecastItem
import com.fedar.weatherapp.model.ForecastResponse
import com.fedar.weatherapp.model.WeatherResponse
import com.fedar.weatherapp.repository.WeatherRepository
import com.fedar.weatherapp.util.formatDate
import kotlinx.browser.document
import kotlinx.browser.localStorage
import kotlinx.coroutines.launch
import org.jetbrains.skiko.wasm.onWasmReady

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val root = document.getElementById("root") ?: error("Root element not found")
    onWasmReady {
        ComposeViewport(root) {
            MaterialTheme {
                App()
            }
        }
    }
}

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Cities) }

    when (currentScreen) {
        is Screen.Cities -> CitiesScreen(onCityClick = { city ->
            currentScreen = Screen.Detail(city)
        })
        is Screen.Detail -> {
            val city = (currentScreen as Screen.Detail).city
            DetailScreen(city = city, onBack = { currentScreen = Screen.Cities })
        }
    }
}

sealed class Screen {
    object Cities : Screen()
    data class Detail(val city: String) : Screen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitiesScreen(onCityClick: (String) -> Unit) {
    var inputCity by remember { mutableStateOf("") }
    var savedCities by remember {
        val raw = localStorage.getItem("city_list") ?: ""
        mutableStateOf(raw.split(",").filter { it.isNotBlank() }.toSet())
    }
    val repository = remember { WeatherRepository(cache = WeatherCache()) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Weather App",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = inputCity,
            onValueChange = { inputCity = it },
            label = { Text("Enter city name") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val city = inputCity.trim()
                if (city.isNotBlank()) {
                    scope.launch {
                        val weather = repository.getCurrentWeather(city)
                        if (weather != null) {
                            savedCities = savedCities + city.lowercase()
                            localStorage.setItem("city_list", savedCities.joinToString(","))
                            inputCity = ""
                        }
                    }
                }
            },
            modifier = Modifier.padding(top = 8.dp),
            enabled = inputCity.isNotBlank()
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add")
            Spacer(Modifier.width(4.dp))
            Text("Add")
        }

        Spacer(Modifier.height(16.dp))

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val columns = when {
                maxWidth < 600.dp -> 1
                maxWidth < 900.dp -> 2
                else -> 3
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(savedCities.toList()) { city ->
                    CityCard(city = city, repository = repository, onClick = { onCityClick(city) })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityCard(city: String, repository: WeatherRepository, onClick: () -> Unit) {
    var weather by remember { mutableStateOf<WeatherResponse?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(city) {
        isLoading = true
        weather = repository.getCurrentWeather(city)
        isLoading = false
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = city.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                style = MaterialTheme.typography.titleMedium
            )
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                weather?.let {
                    Text(text = "${it.main.temp}°C", fontSize = 18.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(city: String, onBack: () -> Unit) {
    val repository = remember { WeatherRepository(cache = WeatherCache()) }
    var weather by remember { mutableStateOf<WeatherResponse?>(null) }
    var forecast by remember { mutableStateOf<ForecastResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(city) {
        weather = repository.getCurrentWeather(city)
        forecast = repository.getForecast(city)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weather Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                weather?.let { w ->
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        CurrentWeatherContent(w)
                    }

                    Spacer(Modifier.height(24.dp))

                    Text(
                        text = "5-Day Forecast",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    val dailyItems = forecast?.list?.filterIndexed { index, _ ->
                        index % 8 == 0
                    }?.take(5) ?: emptyList()

                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val columns = when {
                            maxWidth < 600.dp -> 1
                            maxWidth < 900.dp -> 2
                            else -> 3
                        }
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(columns),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(dailyItems) { item ->
                                ForecastCard(item = item)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CurrentWeatherContent(weather: WeatherResponse) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = weather.name, style = MaterialTheme.typography.headlineSmall)
        Text(
            text = "${weather.main.temp}°C",
            fontSize = 48.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Text(
            text = weather.weather.firstOrNull()?.description?.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase() else it.toString()
            } ?: "",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WeatherProperty("Feels like", "${weather.main.feelsLike}°C")
            WeatherProperty("Humidity", "${weather.main.humidity}%")
            WeatherProperty("Wind", "${weather.wind.speed} m/s")
        }
    }
}

@Composable
fun ForecastCard(item: ForecastItem) {
    val dateFormat = remember { formatDate(item.dt) }
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = dateFormat,
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
}

@Composable
fun WeatherProperty(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}