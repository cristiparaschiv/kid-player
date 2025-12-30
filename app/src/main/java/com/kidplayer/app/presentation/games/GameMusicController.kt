package com.kidplayer.app.presentation.games

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import timber.log.Timber

/**
 * Composable that manages background music based on navigation.
 * Music plays on all screens EXCEPT:
 * - Splash screen (startup)
 * - Player screen (video playback)
 */
@Composable
fun GameMusicController(
    navController: NavController,
    musicManager: GameMusicManager
) {
    var currentRoute by remember { mutableStateOf<String?>(null) }
    var musicStarted by remember { mutableStateOf(false) }

    // Listen to navigation changes
    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            val newRoute = destination.route
            val wasOnPlayer = currentRoute?.startsWith("player") == true
            val isOnPlayer = newRoute?.startsWith("player") == true
            val isOnSplash = newRoute?.startsWith("splash") == true

            Timber.d("Music Controller: route changed from $currentRoute to $newRoute")

            when {
                // Don't start music on splash screen
                isOnSplash -> {
                    Timber.d("On splash screen - not starting music yet")
                }
                // Stop music when entering player (video)
                isOnPlayer && !wasOnPlayer -> {
                    Timber.d("Entering player - stopping music")
                    musicManager.stopMusic()
                }
                // Resume music when leaving player
                !isOnPlayer && wasOnPlayer -> {
                    Timber.d("Leaving player - starting music")
                    musicManager.startMusic()
                    musicStarted = true
                }
                // Start music on first non-splash, non-player screen
                !musicStarted && !isOnPlayer && !isOnSplash -> {
                    Timber.d("First content screen - starting music")
                    musicManager.startMusic()
                    musicStarted = true
                }
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

/**
 * Extension function to check if currently on player screen
 */
fun String?.isPlayerRoute(): Boolean {
    return this?.startsWith("player") == true
}
