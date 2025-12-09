package com.example.fredmobile.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.fredmobile.R

/**
 * Simple model for an item in the bottom navigation bar.
 *
 * @param route Navigation route associated with this item.
 * @param label Text label shown under the icon.
 * @param iconRes Drawable resource ID for the icon.
 */
private data class BottomNavItem(
    val route: String,
    val label: String,
    val iconRes: Int
)

/**
 * Bottom navigation bar for the main app screens.
 *
 * Displays navigation items for Home, Incident, Sites, and History.
 * Uses the provided [NavController] to navigate between destinations
 * and preserves state when switching tabs.
 */
@Composable
fun FredBottomBar(navController: NavController) {
    val items = listOf(
        BottomNavItem(Routes.HOME, "Home", R.drawable.ic_home),
        BottomNavItem(Routes.INCIDENT, "Incident", R.drawable.ic_incident),
        BottomNavItem(Routes.SITES, "Sites", R.drawable.ic_sites),
        BottomNavItem(Routes.HISTORY, "History", R.drawable.ic_history)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination: NavDestination? = navBackStackEntry?.destination

    NavigationBar {
        items.forEach { item ->
            val selected =
                currentDestination?.hierarchy?.any { it.route == item.route } == true

            NavigationBarItem(
                selected = selected,
                onClick = {
                    // Avoid re-navigating when the current tab is already selected.
                    if (!selected) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
