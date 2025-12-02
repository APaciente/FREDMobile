package com.example.fredmobile.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fredmobile.auth.AuthViewModel
import com.example.fredmobile.ui.screens.AuthScreen
import com.example.fredmobile.ui.screens.CheckInScreen
import com.example.fredmobile.ui.screens.HistoryScreen
import com.example.fredmobile.ui.screens.HomeScreen
import com.example.fredmobile.ui.screens.IncidentScreen
import com.example.fredmobile.ui.screens.SettingsScreen
import com.example.fredmobile.ui.screens.SitesScreen

/**
 * Central list of route names used by the navigation graph.
 * Keeping routes in one place makes it easier to refactor later.
 */
object Routes {
    const val AUTH = "auth"
    const val HOME = "home"
    const val SITES = "sites"
    const val CHECK_IN = "check_in"
    const val INCIDENT = "incident"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
}

/**
 * Top-level navigation graph for FRED.
 *
 * Defines all high-level routes (home, sites, check-in, incidents, history,
 * settings). Milestone 1 uses only these screens with mock data. In later
 * milestones we will extend this to include authentication flows and
 * detail screens (site details, incident details, etc.).
 */
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel()
) {
    val startDestination = if (authViewModel.isLoggedIn) {
        Routes.HOME
    } else {
        Routes.AUTH
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.AUTH) {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.AUTH) { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }

        composable(Routes.HOME) { HomeScreen(navController) }
        composable(Routes.SITES) { SitesScreen(navController) }
        composable(Routes.CHECK_IN) { CheckInScreen(navController) }
        composable(Routes.INCIDENT) { IncidentScreen(navController) }
        composable(Routes.HISTORY) { HistoryScreen(navController) }
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
