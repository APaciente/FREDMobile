package com.example.fredmobile.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fredmobile.auth.AuthViewModel
import com.example.fredmobile.ui.checkin.CheckInViewModel
import com.example.fredmobile.ui.location.LocationViewModel
import com.example.fredmobile.ui.navigation.Routes
import com.example.fredmobile.ui.screens.AdminScreen
import com.example.fredmobile.ui.screens.AuthScreen
import com.example.fredmobile.ui.screens.CheckInScreen
import com.example.fredmobile.ui.screens.HistoryScreen
import com.example.fredmobile.ui.screens.HomeScreen
import com.example.fredmobile.ui.screens.IncidentScreen
import com.example.fredmobile.ui.screens.SettingsScreen
import com.example.fredmobile.ui.screens.SitesScreen
import com.example.fredmobile.ui.sites.SitesViewModel
import com.example.fredmobile.ui.weather.WeatherViewModel

/**
 * Top-level navigation graph for the FRED Mobile app.
 *
 * Starts at the authentication screen and routes to the main app
 * screens once a user is signed in.
 */
@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    val authViewModel: AuthViewModel = viewModel()
    val checkInViewModel: CheckInViewModel = viewModel()
    val weatherViewModel: WeatherViewModel = viewModel()
    val locationViewModel: LocationViewModel = viewModel()
    val sitesViewModel: SitesViewModel = viewModel()

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
            HomeScreen(
                navController = navController,
                checkInViewModel = checkInViewModel,
                weatherViewModel = weatherViewModel,
                locationViewModel = locationViewModel
            )
        }

        composable(Routes.SITES) {
            SitesScreen(
                navController = navController,
                sitesViewModel = sitesViewModel
            )
        }

        composable(Routes.CHECKIN) {
            CheckInScreen(
                navController = navController,
                checkInViewModel = checkInViewModel,
                weatherViewModel = weatherViewModel,
                locationViewModel = locationViewModel
            )
        }

        composable(Routes.INCIDENT) {
            IncidentScreen(
                navController = navController,
                incidentViewModel = viewModel(),
                locationViewModel = locationViewModel
            )
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

        composable(Routes.ADMIN) {
            AdminScreen(navController = navController)
        }
    }
}
