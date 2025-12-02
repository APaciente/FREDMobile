package com.example.fredmobile.data.remote

import com.example.fredmobile.model.weather.CurrentWeatherResponse
import com.example.fredmobile.model.weather.ForecastResponse
import com.example.fredmobile.model.weather.AirQualityResponse
import com.example.fredmobile.model.weather.AlertsResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service for accessing weather data from OpenWeather API.
 * Milestone 2: Uses 4 endpoints (current, forecast, alerts, air quality).
 */
interface WeatherApiService {

    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): CurrentWeatherResponse

    @GET("forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): ForecastResponse

    @GET("air_pollution")
    suspend fun getAirQuality(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): AirQualityResponse

    @GET("alerts") // not real endpoint, we simulate or use OneCall 3.0
    suspend fun getWeatherAlerts(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): AlertsResponse
}
