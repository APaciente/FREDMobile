package com.example.fredmobile.ui.weather

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fredmobile.data.remote.WeatherRepository
import kotlinx.coroutines.launch

/**
 * ViewModel that loads weather, forecast, and air quality for a site.
 *
 * Milestone 2:
 *  - Called from Check-In screen.
 *  - Uses three endpoints:
 *      1) Current weather
 *      2) Forecast
 *      3) Air quality (AQI)
 *
 * Milestone 3:
 *  - Supports units ("metric" or "imperial") based on Settings.
 *  - Exposes a small forecast strip via [forecastItems].
 */
class WeatherViewModel(
    private val repo: WeatherRepository = WeatherRepository()
) : ViewModel() {

    var uiState by mutableStateOf(WeatherUiState())
        private set

    fun loadWeatherForSite(lat: Double, lon: Double, units: String) {
        uiState = uiState.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                // 1) Current weather
                val current = repo.getCurrentWeather(lat, lon, units)

                // 2) Air quality
                val air = repo.getAirQuality(lat, lon)

                // 3) Forecast (next few hours)
                val forecast = repo.getForecast(lat, lon, units)

                val temp = current.main.temp
                val desc = current.weather.firstOrNull()
                    ?.description
                    ?.replaceFirstChar { it.uppercase() }

                val aqi = air.list.firstOrNull()?.main?.aqi

                // Next forecast item (e.g., next 3 hours)
                val nextItem = forecast.list.firstOrNull()
                val nextTemp = nextItem?.main?.temp
                val nextDesc = nextItem?.weather?.firstOrNull()
                    ?.description
                    ?.replaceFirstChar { it.uppercase() }

                // Build a small forecast strip (first 5 3-hour slots)
                val forecastItems = forecast.list
                    .take(5) // 5 upcoming time slots
                    .mapIndexed { index, item ->
                        val hoursAhead = (index + 1) * 3
                        val label = "In ${hoursAhead}h"

                        ForecastItemUi(
                            timeLabel = label,
                            temp = item.main.temp,
                            description = item.weather.firstOrNull()
                                ?.description
                                ?.replaceFirstChar { it.uppercase() }
                        )
                    }

                uiState = uiState.copy(
                    isLoading = false,
                    temperature = temp,
                    description = desc,
                    aqi = aqi,
                    nextTemp = nextTemp,
                    nextDescription = nextDesc,
                    forecastItems = forecastItems
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Weather not available right now.",
                    forecastItems = emptyList()
                )
            }
        }
    }
}