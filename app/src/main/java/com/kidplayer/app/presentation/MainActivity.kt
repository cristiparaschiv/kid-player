package com.kidplayer.app.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import androidx.navigation.compose.rememberNavController
import com.kidplayer.app.data.local.LanguageManager
import com.kidplayer.app.data.local.ScreenTimeManager
import com.kidplayer.app.data.network.NetworkMonitor
import com.kidplayer.app.presentation.games.GameMusicManager
import com.kidplayer.app.presentation.localization.LocalizedApp
import com.kidplayer.app.presentation.navigation.KidPlayerNavGraphPhase6
import com.kidplayer.app.presentation.navigation.MainScaffold
import com.kidplayer.app.presentation.navigation.Screen
import com.kidplayer.app.presentation.navigation.shouldShowBottomNav
import com.kidplayer.app.presentation.theme.KidPlayerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Main Activity for Kid Player App
 *
 * This activity hosts the entire Compose-based UI and navigation.
 * It uses Hilt for dependency injection and is configured for landscape orientation.
 */
@UnstableApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var screenTimeManager: ScreenTimeManager

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @Inject
    lateinit var gameMusicManager: GameMusicManager

    @Inject
    lateinit var languageManager: LanguageManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.d("MainActivity: onCreate")

        // Initialize screen time manager
        lifecycleScope.launch {
            screenTimeManager.initialize()
        }

        // Start network monitoring
        networkMonitor.startMonitoring()
        Timber.d("Network monitoring started")

        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            // Wrap with LocalizedApp for runtime language switching
            LocalizedApp(languageManager = languageManager) {
                KidPlayerTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val navController = rememberNavController()
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry?.destination?.route

                        // Phase 6: Use MainScaffold with bottom navigation
                        // Persistent Login: Start with Splash screen for auto-login
                        MainScaffold(
                            navController = navController,
                            showBottomNav = shouldShowBottomNav(currentRoute)
                        ) { modifier ->
                            KidPlayerNavGraphPhase6(
                                navController = navController,
                                modifier = modifier,
                                startDestination = Screen.Splash.route,
                                musicManager = gameMusicManager
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Timber.d("MainActivity: onPause - Stopping network monitoring")
        networkMonitor.stopMonitoring()
        // Pause game music when app goes to background
        gameMusicManager.pauseMusic()
    }

    override fun onResume() {
        super.onResume()
        Timber.d("MainActivity: onResume - Starting network monitoring")
        networkMonitor.startMonitoring()
        // Resume game music if it was playing
        gameMusicManager.resumeMusic()
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("MainActivity: onDestroy")
        networkMonitor.stopMonitoring()
        // Stop and release music resources
        gameMusicManager.stopMusic()
    }
}
