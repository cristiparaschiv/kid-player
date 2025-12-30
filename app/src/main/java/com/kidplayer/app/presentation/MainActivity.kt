package com.kidplayer.app.presentation

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import androidx.navigation.compose.rememberNavController
import com.kidplayer.app.data.local.AppLanguage
import com.kidplayer.app.data.local.LanguageManager
import com.kidplayer.app.data.local.ScreenTimeManager
import com.kidplayer.app.data.local.SecurePreferences
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
import java.util.Locale
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

    // Track the language used when the activity was created
    private var initialLanguageCode: String? = null

    companion object {
        private const val PREFS_NAME = "kidplayer_secure_prefs"
        private const val KEY_APP_LANGUAGE = "app_language"

        /**
         * Get the saved language code from encrypted preferences
         * Called from attachBaseContext before Hilt injection is available
         */
        fun getSavedLanguageCode(context: Context): String {
            return try {
                val masterKey = androidx.security.crypto.MasterKey.Builder(context)
                    .setKeyScheme(androidx.security.crypto.MasterKey.KeyScheme.AES256_GCM)
                    .build()

                val prefs = androidx.security.crypto.EncryptedSharedPreferences.create(
                    context,
                    PREFS_NAME,
                    masterKey,
                    androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )

                prefs.getString(KEY_APP_LANGUAGE, "en") ?: "en"
            } catch (e: Exception) {
                Timber.e(e, "Failed to read language from prefs, using default")
                "en"
            }
        }

        /**
         * Wrap context with the specified locale
         */
        fun wrapContextWithLocale(context: Context, languageCode: String): Context {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)

            val config = context.resources.configuration
            config.setLocale(locale)
            config.setLocales(android.os.LocaleList(locale))

            return context.createConfigurationContext(config)
        }
    }

    /**
     * Apply saved locale to the base context before Activity is created
     * This ensures all resources are loaded with the correct locale
     */
    override fun attachBaseContext(newBase: Context) {
        val languageCode = getSavedLanguageCode(newBase)
        val localizedContext = wrapContextWithLocale(newBase, languageCode)
        super.attachBaseContext(localizedContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.d("MainActivity: onCreate")

        // Store the initial language for detecting changes
        if (initialLanguageCode == null) {
            initialLanguageCode = languageManager.getLanguageCode()
            Timber.d("Initial language set to: $initialLanguageCode")
        }

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
            // Observe language changes and recreate activity when language changes
            val currentLanguage by languageManager.currentLanguage.collectAsState()

            LaunchedEffect(currentLanguage) {
                val currentCode = currentLanguage.code
                val initialCode = initialLanguageCode
                if (initialCode != null && currentCode != initialCode) {
                    Timber.d("Language changed from $initialCode to $currentCode, recreating activity")
                    initialLanguageCode = currentCode // Update to prevent loop on next create
                    recreate()
                }
            }

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
