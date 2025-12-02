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
 */
class WeatherViewModel(
    private val repo: WeatherRepository = WeatherRepository()
) : ViewModel() {

    var uiState by mutableStateOf(WeatherUiState())
        private set

    fun loadWeatherForSite(lat: Double, lon: Double) {
        uiState = uiState.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                // 1) Current weather
                val current = repo.getCurrentWeather(lat, lon)

                // 2) Air quality
                val air = repo.getAirQuality(lat, lon)

                // 3) Forecast (next few hours)
                val forecast = repo.getForecast(lat, lon)

                val temp = current.main.temp
                val desc = current.weather.firstOrNull()
                    ?.description
                    ?.replaceFirstChar { it.uppercase() }

                val aqi = air.list.firstOrNull()?.main?.aqi

                // Take the very next forecast item (e.g., next 3 hours)
                val nextItem = forecast.list.firstOrNull()
                val nextTemp = nextItem?.main?.temp
                val nextDesc = nextItem?.weather?.firstOrNull()
                    ?.description
                    ?.replaceFirstChar { it.uppercase() }

                uiState = uiState.copy(
                    isLoading = false,
                    temperature = temp,
                    description = desc,
                    aqi = aqi,
                    nextTemp = nextTemp,
                    nextDescription = nextDesc
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Weather not available right now."
                )
            }
        }
    }
}
