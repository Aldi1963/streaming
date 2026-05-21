package com.sonzaix.streaming.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.sonzaix.streaming.presentation.screens.api_tester.ApiTesterScreen
import com.sonzaix.streaming.presentation.screens.detail.DetailScreen
import com.sonzaix.streaming.presentation.screens.favorite.FavoriteScreen
import com.sonzaix.streaming.presentation.screens.history.HistoryScreen
import com.sonzaix.streaming.presentation.screens.home.HomeScreen
import com.sonzaix.streaming.presentation.screens.player.PlayerScreen
import com.sonzaix.streaming.presentation.screens.popular.PopularScreen
import com.sonzaix.streaming.presentation.screens.search.SearchScreen
import com.sonzaix.streaming.presentation.screens.settings.SettingsScreen
import com.sonzaix.streaming.presentation.screens.splash.SplashScreen

data class BottomNavItem(val route: String, val label: String, val icon: @Composable () -> Unit)

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    val bottomItems = listOf(
        BottomNavItem(Routes.HOME, "Home") { Icon(Icons.Default.Home, contentDescription = "Home") },
        BottomNavItem(Routes.SEARCH, "Search") { Icon(Icons.Default.Search, contentDescription = "Search") },
        BottomNavItem(Routes.POPULAR, "Popular") { Icon(Icons.Default.TrendingUp, contentDescription = "Popular") },
        BottomNavItem(Routes.FAVORITE, "Favorite") { Icon(Icons.Default.Favorite, contentDescription = "Favorite") },
        BottomNavItem(Routes.SETTINGS, "Settings") { Icon(Icons.Default.Settings, contentDescription = "Settings") }
    )

    val showBottomBar = currentRoute in bottomItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    bottomItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = item.icon,
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.SPLASH) {
                SplashScreen(onNavigateHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                })
            }
            composable(Routes.HOME) {
                HomeScreen(
                    onDramaClick = { provider, id -> navController.navigate(Routes.detail(provider, id)) },
                    onHistoryClick = { navController.navigate(Routes.HISTORY) },
                    onSearchClick = { navController.navigate(Routes.SEARCH) }
                )
            }
            composable(Routes.SEARCH) {
                SearchScreen(onDramaClick = { provider, id -> navController.navigate(Routes.detail(provider, id)) })
            }
            composable(Routes.POPULAR) {
                PopularScreen(onDramaClick = { provider, id -> navController.navigate(Routes.detail(provider, id)) })
            }
            composable(Routes.FAVORITE) {
                FavoriteScreen(onDramaClick = { provider, id -> navController.navigate(Routes.detail(provider, id)) })
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(onApiTester = { navController.navigate(Routes.API_TESTER) })
            }
            composable(Routes.HISTORY) {
                HistoryScreen(
                    onItemClick = { provider, dramaId, episodeId, epNum ->
                        navController.navigate(Routes.player(provider, dramaId, episodeId, epNum))
                    }
                )
            }
            composable(Routes.API_TESTER) { ApiTesterScreen() }
            composable(
                Routes.DETAIL,
                arguments = listOf(
                    navArgument("provider") { type = NavType.StringType },
                    navArgument("dramaId") { type = NavType.StringType }
                )
            ) {
                DetailScreen(
                    onEpisodeClick = { provider, dramaId, episodeId, epNum ->
                        navController.navigate(Routes.player(provider, dramaId, episodeId, epNum))
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                Routes.PLAYER,
                arguments = listOf(
                    navArgument("provider") { type = NavType.StringType },
                    navArgument("dramaId") { type = NavType.StringType },
                    navArgument("episodeId") { type = NavType.StringType },
                    navArgument("episodeNumber") { type = NavType.IntType }
                )
            ) {
                PlayerScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
