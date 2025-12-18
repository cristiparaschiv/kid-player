package com.kidplayer.app.presentation.util

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput

/**
 * Animation utilities for Kid Player app
 * Provides playful, bouncy animations for a kid-friendly experience
 */

/**
 * Bouncy click modifier that scales down on press with spring physics
 * Creates a fun, tactile feel for interactive elements
 *
 * @param scaleOnPress The scale factor when pressed (0.92f = 92% of original size)
 * @param enabled Whether the click is enabled
 * @param onClick The action to perform on click
 */
fun Modifier.bouncyClickable(
    scaleOnPress: Float = 0.92f,
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleOnPress else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "bouncy_scale"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(enabled) {
            if (enabled) {
                awaitPointerEventScope {
                    while (true) {
                        awaitFirstDown(requireUnconsumed = false)
                        isPressed = true
                        val up = waitForUpOrCancellation()
                        isPressed = false
                        if (up != null) {
                            onClick()
                        }
                    }
                }
            }
        }
}

/**
 * Bouncy click modifier with interaction source for ripple effects
 * Use this when you want both the bounce animation and Material ripple
 *
 * @param interactionSource Interaction source for ripple
 * @param scaleOnPress The scale factor when pressed
 * @param enabled Whether the click is enabled
 * @param onClick The action to perform on click
 */
@Composable
fun Modifier.bouncyClickableWithRipple(
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    scaleOnPress: Float = 0.92f,
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleOnPress else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "bouncy_scale_ripple"
    )

    return this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = interactionSource,
            indication = null, // Remove ripple for cleaner bouncy effect
            enabled = enabled
        ) {
            onClick()
        }
        .pointerInput(enabled) {
            if (enabled) {
                awaitPointerEventScope {
                    while (true) {
                        awaitFirstDown(requireUnconsumed = false)
                        isPressed = true
                        waitForUpOrCancellation()
                        isPressed = false
                    }
                }
            }
        }
}

/**
 * Pulse animation modifier for attention-grabbing elements
 * Creates a gentle pulsing scale effect
 *
 * @param minScale Minimum scale in pulse cycle
 * @param maxScale Maximum scale in pulse cycle
 */
@Composable
fun Modifier.pulseAnimation(
    minScale: Float = 0.95f,
    maxScale: Float = 1.05f
): Modifier {
    var isPulsing by remember { mutableStateOf(true) }

    val scale by animateFloatAsState(
        targetValue = if (isPulsing) maxScale else minScale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessVeryLow
        ),
        finishedListener = { isPulsing = !isPulsing },
        label = "pulse_scale"
    )

    return this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * Spring animation specs for consistent animations across the app
 */
object KidPlayerAnimations {
    // Bouncy spring for interactive elements
    val bouncySpring = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )

    // Gentle spring for content transitions
    val gentleSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow
    )

    // Snappy spring for quick responses
    val snappySpring = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )

    // Slow spring for dramatic entrances
    val slowSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessVeryLow
    )
}
