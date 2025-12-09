package com.example.fredmobile.util

/**
 * Maps a short weather description string to a representative emoji.
 *
 * @param description text such as "clear sky", "light rain", or "snow".
 * @return an emoji matching the main condition, or a cloud if unknown.
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