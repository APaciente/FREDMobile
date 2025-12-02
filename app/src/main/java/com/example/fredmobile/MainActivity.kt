package com.example.fredmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.fredmobile.ui.AppNavHost
import com.example.fredmobile.ui.theme.FredmobileTheme

/**
 * Main entry point for the FRED mobile app.
 *
 * Hosts the Compose UI by setting the app theme and starting [AppNavHost].
 * In later milestones this activity will also handle things like app-level
 * deep links and notification navigation.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FredmobileTheme {
                AppNavHost()
            }
        }
    }
}
