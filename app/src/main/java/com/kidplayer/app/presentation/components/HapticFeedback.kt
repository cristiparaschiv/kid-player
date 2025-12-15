package com.kidplayer.app.presentation.components

import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

/**
 * Haptic feedback utility for Kid Player app
 * Provides tactile feedback on user interactions to improve accessibility
 * and create a more engaging experience for children
 */
class HapticFeedback(private val view: View) {

    /**
     * Perform a light haptic feedback (e.g., for button presses)
     */
    fun performLight() {
        view.performHapticFeedback(
            HapticFeedbackConstants.CLOCK_TICK,
            HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
        )
    }

    /**
     * Perform a medium haptic feedback (e.g., for toggle actions)
     */
    fun performMedium() {
        view.performHapticFeedback(
            HapticFeedbackConstants.CONTEXT_CLICK,
            HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
        )
    }

    /**
     * Perform a strong haptic feedback (e.g., for important confirmations)
     */
    fun performStrong() {
        view.performHapticFeedback(
            HapticFeedbackConstants.LONG_PRESS,
            HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
        )
    }

    /**
     * Perform keyboard key press haptic feedback
     */
    fun performKeyPress() {
        view.performHapticFeedback(
            HapticFeedbackConstants.KEYBOARD_TAP,
            HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
        )
    }

    /**
     * Perform confirmation haptic feedback (e.g., successful action)
     */
    fun performConfirm() {
        view.performHapticFeedback(
            HapticFeedbackConstants.CONFIRM,
            HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
        )
    }

    /**
     * Perform rejection haptic feedback (e.g., error or cancel)
     */
    fun performReject() {
        view.performHapticFeedback(
            HapticFeedbackConstants.REJECT,
            HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
        )
    }
}

/**
 * Remember a HapticFeedback instance for the current view
 */
@Composable
fun rememberHapticFeedback(): HapticFeedback {
    val view = LocalView.current
    return remember(view) { HapticFeedback(view) }
}

/**
 * Extension function to safely perform haptic feedback on nullable HapticFeedback
 */
fun HapticFeedback?.performLightOrNull() {
    this?.performLight()
}

fun HapticFeedback?.performMediumOrNull() {
    this?.performMedium()
}

fun HapticFeedback?.performStrongOrNull() {
    this?.performStrong()
}
