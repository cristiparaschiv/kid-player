package com.kidplayer.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kidplayer.app.presentation.browse.BrowseScreen
import com.kidplayer.app.presentation.downloaded.DownloadedScreen
import com.kidplayer.app.presentation.favorites.FavoritesScreen
import com.kidplayer.app.presentation.home.HomeScreen
import com.kidplayer.app.presentation.onboarding.SetupScreen
import com.kidplayer.app.presentation.player.PlayerScreen
import com.kidplayer.app.presentation.restrictions.TimeLimitReachedScreen
import com.kidplayer.app.presentation.search.SearchScreen
import com.kidplayer.app.presentation.settings.SettingsScreenNew
import com.kidplayer.app.presentation.splash.SplashScreen
import timber.log.Timber

/**
 * Helper composable that provides a navigation guard for video playback
 * Returns a lambda that checks screen time before navigating to player
 *
 * @param replaceExisting If true, replaces the current player screen instead of stacking
 */
@Composable
fun rememberVideoNavigationHandler(
    navController: NavHostController,
    replaceExisting: Boolean = false,
    screenTimeViewModel: ScreenTimeNavigationViewModel = hiltViewModel()
): (String) -> Unit {
    val isTimeLimitReached by screenTimeViewModel.isTimeLimitReached.collectAsState()

    // Refresh screen time status
    LaunchedEffect(Unit) {
        screenTimeViewModel.checkScreenTimeStatus()
    }

    return { videoId: String ->
        if (isTimeLimitReached) {
            Timber.d("Navigation to player blocked - time limit reached")
            // Don't navigate - the player screen will show the time limit overlay
        } else {
            Timber.d("Navigating to player for video: $videoId")
            if (replaceExisting) {
                // Replace current player with new video (for play next)
                navController.navigate(Screen.Player.createRoute(videoId)) {
                    popUpTo(Screen.Player.route) { inclusive = true }
                }
            } else {
                // Normal navigation
                navController.navigate(Screen.Player.createRoute(videoId))
            }
        }
    }
}

/**
 * Phase 6 Navigation Graph
 * Updated with Home, Search, and Favorites screens
 * Persistent Login: Added Splash screen with auto-login
 *
 * Navigation flow:
 * 1. Splash (auto-login) -> Home (if authenticated) or Setup (if not)
 * 2. Setup (first launch) -> Home
 * 3. Home/Browse/Search/Favorites -> Player (on video click)
 * 4. Any screen -> Settings (parent access)
 * 5. Player -> Home (on completion/back)
 * 6. Any screen -> TimeLimitReached (when time limit reached)
 * 7. Settings (logout) -> Setup
 */
@UnstableApi
@Composable
fun KidPlayerNavGraphPhase6(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Splash Screen with Auto-Login
        composable(route = Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    // Session is valid, navigate to home and clear back stack
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToSetup = {
                    // No valid session, navigate to setup and clear back stack
                    navController.navigate(Screen.Setup.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Setup/Onboarding Screen
        composable(route = Screen.Setup.route) {
            SetupScreen(
                onSetupComplete = {
                    // Navigate to home and clear back stack
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Setup.route) { inclusive = true }
                    }
                }
            )
        }

        // Home Screen (NEW in Phase 6)
        composable(route = Screen.Home.route) {
            val navigateToVideo = rememberVideoNavigationHandler(navController)
            HomeScreen(
                onVideoClick = navigateToVideo,
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },
                onSeeAllClick = { section ->
                    // Navigate to appropriate screen based on section
                    when (section) {
                        "favorites" -> navController.navigate(Screen.Favorites.route)
                        "downloads", "recently_added", "continue_watching" -> {
                            navController.navigate(Screen.Browse.route)
                        }
                    }
                }
            )
        }

        // Browse Screen
        composable(route = Screen.Browse.route) {
            val navigateToVideo = rememberVideoNavigationHandler(navController)
            BrowseScreen(
                onVideoClick = navigateToVideo,
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        // Downloaded Screen (NEW in Phase 6)
        composable(route = Screen.Downloaded.route) {
            val navigateToVideo = rememberVideoNavigationHandler(navController)
            DownloadedScreen(
                onVideoClick = navigateToVideo
            )
        }

        // Search Screen (NEW in Phase 6)
        composable(route = Screen.Search.route) {
            val navigateToVideo = rememberVideoNavigationHandler(navController)
            SearchScreen(
                onVideoClick = navigateToVideo,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Favorites Screen (NEW in Phase 6)
        composable(route = Screen.Favorites.route) {
            val navigateToVideo = rememberVideoNavigationHandler(navController)
            FavoritesScreen(
                onVideoClick = navigateToVideo,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Player Screen
        composable(
            route = Screen.Player.route,
            arguments = listOf(
                navArgument("videoId") {
                    type = NavType.StringType
                }
            )
        ) {
            val navigateToNextVideo = rememberVideoNavigationHandler(
                navController = navController,
                replaceExisting = true
            )
            PlayerScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onPlayNext = { videoId ->
                    // Use navigation guard for play next with screen replacement
                    navigateToNextVideo(videoId)
                }
            )
        }

        // Settings Screen (Parent-only, PIN protected)
        composable(route = Screen.Settings.route) {
            SettingsScreenNew(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToSetup = {
                    // Logout: Navigate to Setup and clear entire back stack
                    navController.navigate(Screen.Setup.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Time Limit Reached Screen
        composable(
            route = Screen.TimeLimitReached.route,
            arguments = listOf(
                navArgument("limitMinutes") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val limitMinutes = backStackEntry.arguments?.getInt("limitMinutes") ?: 60

            TimeLimitReachedScreen(
                limitMinutes = limitMinutes,
                onParentOverride = {
                    // Navigate to settings for parent to modify limits
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
    }
}

/**
 * Determines if bottom navigation should be shown for the current route
 */
fun shouldShowBottomNav(currentRoute: String?): Boolean {
    return currentRoute in listOf(
        Screen.Home.route,
        Screen.Browse.route,
        Screen.Downloaded.route,
        Screen.Search.route,
        Screen.Favorites.route
    )
}
