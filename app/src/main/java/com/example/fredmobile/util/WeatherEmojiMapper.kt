package com.example.fredmobile.util

/**
 * Very simple mapping from OpenWeather-style descriptions to emojis.
 */
fun mapWeatherDescriptionToEmoji(description: String?): String {
    val d = description?.lowercase() ?: return "☁️"

    return when {
        "clear" in d || "sun" in d -> "☀️"
        "cloud" in d -> "☁️"
        "rain" in d || "drizzle" in d -> "🌧️"
        "thunder" in d || "storm" in d -> "⛈️"
        "snow" in d -> "❄️"
        "mist" in d || "fog" in d || "haze" in d -> "🌫️"
        else -> "☁️"
    }
}
