package com.example.fredmobile.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fredmobile.auth.AuthViewModel
import com.example.fredmobile.ui.navigation.Routes
import com.example.fredmobile.ui.screens.AuthScreen
import com.example.fredmobile.ui.screens.CheckInScreen
import com.example.fredmobile.ui.screens.HistoryScreen
import com.example.fredmobile.ui.screens.HomeScreen
import com.example.fredmobile.ui.screens.IncidentScreen
import com.example.fredmobile.ui.screens.SettingsScreen
import com.example.fredmobile.ui.screens.SitesScreen

/**
 * Top-level navigation graph for the FRED mobile app.
 *
 * Milestone 1:
 *  - Handles navigation between core screens.
 *
 * Milestone 3:
 *  - Integrates authentication flow:
 *      - Starts at [Routes.AUTH]
 *      - Navigates to [Routes.HOME] after sign-in
 *      - Settings screen can trigger sign-out and return to [Routes.AUTH]
 */
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.AUTH
    ) {
        composable(Routes.AUTH) {
            AuthScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable(Routes.HOME) {
            HomeScreen(navController = navController)
        }

        composable(Routes.SITES) {
            SitesScreen(navController = navController)
        }

        composable(Routes.CHECKIN) {
            CheckInScreen(navController = navController)
        }

        composable(Routes.INCIDENT) {
            IncidentScreen(navController = navController)
        }

        composable(Routes.HISTORY) {
            HistoryScreen(navController = navController)
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                navController = navController,
                onSignOut = {
                    authViewModel.signOut()
                    navController.navigate(Routes.AUTH) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }
    }
}
