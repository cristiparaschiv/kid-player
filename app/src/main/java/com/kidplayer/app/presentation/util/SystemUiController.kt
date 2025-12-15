package com.kidplayer.app.presentation.util

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.Window
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * SystemUiController - Utility for managing system UI visibility
 *
 * Provides a clean API for hiding/showing system bars (status bar, navigation bar)
 * with support for different immersive modes.
 *
 * Usage:
 * ```
 * val systemUiController = rememberSystemUiController()
 * DisposableEffect(Unit) {
 *     systemUiController.setSystemBarsVisible(false, isSticky = true)
 *     onDispose {
 *         systemUiController.setSystemBarsVisible(true)
 *     }
 * }
 * ```
 */
class SystemUiController(private val window: Window) {

    private val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

    /**
     * Show or hide system bars (status bar and navigation bar)
     *
     * @param visible Whether to show the system bars
     * @param isSticky If true, system bars will auto-hide after user interaction (sticky immersive)
     *                 If false, system bars stay hidden until explicitly shown (immersive)
     */
    fun setSystemBarsVisible(visible: Boolean, isSticky: Boolean = false) {
        windowInsetsController.apply {
            if (visible) {
                // Show system bars
                show(WindowInsetsCompat.Type.systemBars())
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
            } else {
                // Hide system bars
                hide(WindowInsetsCompat.Type.systemBars())

                // Set behavior for how system bars reappear
                systemBarsBehavior = if (isSticky) {
                    // Sticky immersive: Swipe reveals bars, they auto-hide after a moment
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                } else {
                    // Standard immersive: Bars stay visible until explicitly hidden again
                    WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
                }
            }
        }
    }

    /**
     * Hide only the status bar (keeps navigation bar visible)
     */
    fun setStatusBarVisible(visible: Boolean) {
        windowInsetsController.apply {
            if (visible) {
                show(WindowInsetsCompat.Type.statusBars())
            } else {
                hide(WindowInsetsCompat.Type.statusBars())
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }

    /**
     * Hide only the navigation bar (keeps status bar visible)
     */
    fun setNavigationBarVisible(visible: Boolean) {
        windowInsetsController.apply {
            if (visible) {
                show(WindowInsetsCompat.Type.navigationBars())
            } else {
                hide(WindowInsetsCompat.Type.navigationBars())
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }

    /**
     * Set whether the status bar should have light or dark content
     * @param useDarkIcons If true, status bar icons will be dark (for light backgrounds)
     */
    fun setStatusBarAppearance(useDarkIcons: Boolean) {
        windowInsetsController.isAppearanceLightStatusBars = useDarkIcons
    }

    /**
     * Set whether the navigation bar should have light or dark content
     * @param useDarkIcons If true, navigation bar icons will be dark (for light backgrounds)
     */
    fun setNavigationBarAppearance(useDarkIcons: Boolean) {
        windowInsetsController.isAppearanceLightNavigationBars = useDarkIcons
    }
}

/**
 * Remember a SystemUiController for the current window
 */
@Composable
fun rememberSystemUiController(): SystemUiController? {
    val context = LocalContext.current
    val window = (context as? Activity)?.window
    return window?.let { SystemUiController(it) }
}

/**
 * Composable effect to hide system bars while in composition
 *
 * @param isSticky If true, uses sticky immersive mode (bars auto-hide after swipe)
 *
 * Usage:
 * ```
 * HideSystemBars(isSticky = true)
 * ```
 */
@Composable
fun HideSystemBars(isSticky: Boolean = true) {
    val systemUiController = rememberSystemUiController()

    DisposableEffect(systemUiController) {
        systemUiController?.setSystemBarsVisible(visible = false, isSticky = isSticky)

        onDispose {
            // Restore system bars when leaving composition
            systemUiController?.setSystemBarsVisible(visible = true)
        }
    }
}
