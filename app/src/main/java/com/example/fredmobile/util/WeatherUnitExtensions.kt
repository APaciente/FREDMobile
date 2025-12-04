package com.example.fredmobile.util

/**
 * Convert the user-facing weather unit string to OpenWeather units.
 */
fun String.toOpenWeatherUnits(): String =
    if (this == "Fahrenheit") "imperial" else "metric"

/**
 * Convert the user-facing weather unit string to a temperature suffix.
 */
fun String.toTemperatureSuffix(): String =
    if (this == "Fahrenheit") "°F" else "°C"
