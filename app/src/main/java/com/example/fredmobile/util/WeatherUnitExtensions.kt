package com.example.fredmobile.util

/**
 * Converts a user-facing weather unit label to the OpenWeather units value.
 *
 * @return "imperial" for "Fahrenheit", otherwise "metric".
 */
fun String.toOpenWeatherUnits(): String =
    if (this == "Fahrenheit") "imperial" else "metric"

/**
 * Converts a user-facing weather unit label to a temperature suffix.
 *
 * @return "°F" for "Fahrenheit", otherwise "°C".
 */
fun String.toTemperatureSuffix(): String =
    if (this == "Fahrenheit") "°F" else "°C"
