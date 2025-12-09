package com.example.fredmobile.ui.admin

import com.example.fredmobile.model.firestore.CheckIn

/**
 * UI state for the admin check-in overview.
 *
 * @param isLoading Indicates whether data is currently being loaded.
 * @param errorMessage Optional error message to display.
 * @param todaySummary Latest check-in per user for the current day.
 * @param dailyHistory Recent check-ins for all users.
 */
data class AdminUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val todaySummary: List<CheckIn> = emptyList(),
    val dailyHistory: List<CheckIn> = emptyList()
)
