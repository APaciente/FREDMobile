package com.example.fredmobile.ui.admin

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.fredmobile.model.firestore.CheckIn
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Calendar

/**
 * ViewModel for the admin check-in overview screen.
 *
 * Loads check-in data for all users and exposes it as [AdminUiState].
 */
class AdminViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    var uiState by mutableStateOf(AdminUiState())
        private set

    init {
        loadCheckIns()
    }

    private fun loadCheckIns() {
        uiState = uiState.copy(isLoading = true, errorMessage = null)

        db.collectionGroup("checkins")
            .orderBy("inTime", Query.Direction.DESCENDING)
            .limit(200)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("AdminViewModel", "Error loading team check-ins", e)
                    uiState = uiState.copy(isLoading = false)
                    return@addSnapshotListener
                }

                val docs = snapshot ?: run {
                    uiState = uiState.copy(
                        isLoading = false,
                        todaySummary = emptyList(),
                        dailyHistory = emptyList()
                    )
                    return@addSnapshotListener
                }

                val allCheckIns = docs.documents.mapNotNull { doc ->
                    try {
                        CheckIn(
                            id = doc.id,
                            userId = doc.getString("userId") ?: "",
                            userName = doc.getString("userName") ?: "",
                            siteId = doc.getString("siteId") ?: "",
                            siteName = doc.getString("siteName") ?: "",
                            status = doc.getString("status") ?: "",
                            inTime = doc.getTimestamp("inTime"),
                            outTime = doc.getTimestamp("outTime")
                        )
                    } catch (ex: Exception) {
                        Log.w("AdminViewModel", "Skipping invalid check-in document ${doc.id}", ex)
                        null
                    }
                }

                val todayRows = allCheckIns.filter { ci ->
                    ci.inTime.isTodayLocal()
                }

                val latestPerUserToday = todayRows
                    .groupBy { it.userId.ifBlank { "unknown" } }
                    .values
                    .mapNotNull { list ->
                        list.maxByOrNull { it.inTime?.seconds ?: 0L }
                    }
                    .sortedBy { it.siteName }

                uiState = uiState.copy(
                    isLoading = false,
                    todaySummary = latestPerUserToday,
                    dailyHistory = allCheckIns
                )
            }
    }
}

/**
 * Returns `true` if this [Timestamp] falls on the current day in the device's local time zone.
 */
private fun Timestamp?.isTodayLocal(): Boolean {
    if (this == null) return false

    val tsCal = Calendar.getInstance().apply {
        time = this@isTodayLocal.toDate()
    }
    val todayCal = Calendar.getInstance()

    return tsCal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR) &&
            tsCal.get(Calendar.DAY_OF_YEAR) == todayCal.get(Calendar.DAY_OF_YEAR)
}
