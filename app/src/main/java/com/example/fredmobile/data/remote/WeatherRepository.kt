package com.example.fredmobile.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Repository that provides weather data to UI screens.
 * Uses Retrofit and exposes suspend functions.
 *
 * Now supports units: "metric" (Celsius) or "imperial" (Fahrenheit).
 */
class WeatherRepository {

    private val api = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherApiService::class.java)

    // You can move this to local.properties / BuildConfig later if needed
    private val apiKey = "c4394605ecdb75083cd9926c61dce9d4"

    suspend fun getCurrentWeather(lat: Double, lon: Double, units: String) =
        api.getCurrentWeather(
            lat = lat,
            lon = lon,
            apiKey = apiKey,
            units = units
        )

    suspend fun getForecast(lat: Double, lon: Double, units: String) =
        api.getForecast(
            lat = lat,
            lon = lon,
            apiKey = apiKey,
            units = units
        )

    suspend fun getAirQuality(lat: Double, lon: Double) =
        api.getAirQuality(lat, lon, apiKey)

    suspend fun getAlerts(lat: Double, lon: Double) =
        api.getWeatherAlerts(lat, lon, apiKey)
}
