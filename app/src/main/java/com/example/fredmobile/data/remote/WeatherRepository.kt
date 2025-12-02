package com.example.fredmobile.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Repository that provides weather data to UI screens.
 * Uses Retrofit and exposes suspend functions.
 */
class WeatherRepository {

    private val api = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherApiService::class.java)

    private val apiKey = "c4394605ecdb75083cd9926c61dce9d4"

    suspend fun getCurrentWeather(lat: Double, lon: Double) =
        api.getCurrentWeather(lat, lon, apiKey)

    suspend fun getForecast(lat: Double, lon: Double) =
        api.getForecast(lat, lon, apiKey)

    suspend fun getAirQuality(lat: Double, lon: Double) =
        api.getAirQuality(lat, lon, apiKey)

    suspend fun getAlerts(lat: Double, lon: Double) =
        api.getWeatherAlerts(lat, lon, apiKey)
}
