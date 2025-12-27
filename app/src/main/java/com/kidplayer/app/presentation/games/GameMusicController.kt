package com.kidplayer.app.presentation.games

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController

/**
 * Composable that manages background music based on navigation.
 * Starts music when entering games section and stops when leaving.
 */
@Composable
fun GameMusicController(
    navController: NavController,
    musicManager: GameMusicManager
) {
    var currentRoute by remember { mutableStateOf<String?>(null) }

    // Listen to navigation changes
    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            val newRoute = destination.route
            val wasInGames = currentRoute?.startsWith("games") == true
            val isInGames = newRoute?.startsWith("games") == true

            // Start music when entering games section
            if (isInGames && !wasInGames) {
                musicManager.startMusic()
            }
            // Stop music when leaving games section
            else if (!isInGames && wasInGames) {
                musicManager.stopMusic()
            }

            currentRoute = newRoute
        }

        navController.addOnDestinationChangedListener(listener)

        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }
}

/**
 * Extension function to check if currently in games section
 */
fun String?.isGamesRoute(): Boolean {
    return this?.startsWith("games") == true
}
