package com.kidplayer.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kidplayer.app.data.local.ScreenTimeManager
import com.kidplayer.app.domain.model.AccessRestriction
import com.kidplayer.app.presentation.browse.BrowseScreen
import com.kidplayer.app.presentation.onboarding.SetupScreen
import com.kidplayer.app.presentation.player.PlayerScreen
import com.kidplayer.app.presentation.restrictions.TimeLimitReachedScreen
import com.kidplayer.app.presentation.settings.SettingsScreenNew
import timber.log.Timber

/**
 * Main navigation graph for Kid Player app
 *
 * Navigation flow:
 * 1. Setup (first launch) -> Browse
 * 2. Browse -> Player (on video click)
 * 3. Browse -> Settings (parent access)
 * 4. Player -> Browse (on completion/back)
 * 5. Browse/Player -> TimeLimitReached (when time limit reached)
 */
@UnstableApi
@Composable
fun KidPlayerNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Setup.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Setup/Onboarding Screen
        composable(route = Screen.Setup.route) {
            SetupScreen(
                onSetupComplete = {
                    // Navigate to browse and clear back stack
                    navController.navigate(Screen.Browse.route) {
                        popUpTo(Screen.Setup.route) { inclusive = true }
                    }
                }
            )
        }

        // Browse Screen (Main screen)
        composable(route = Screen.Browse.route) {
            BrowseScreen(
                onVideoClick = { videoId ->
                    navController.navigate(Screen.Player.createRoute(videoId))
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
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
            PlayerScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Settings Screen (Parent-only, PIN protected)
        composable(route = Screen.Settings.route) {
            SettingsScreenNew(
                onNavigateBack = {
                    navController.popBackStack()
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
