package com.kidplayer.app.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kidplayer.app.R
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.delay

/**
 * Feedback type for game responses
 */
enum class FeedbackType {
    CORRECT,
    WRONG
}

/**
 * Animated feedback icon for correct/wrong answers
 * Shows checkmark or cross with bounce animation
 */
@Composable
fun FeedbackIcon(
    type: FeedbackType,
    visible: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    showBackground: Boolean = true,
    onAnimationComplete: (() -> Unit)? = null
) {
    val iconRes = when (type) {
        FeedbackType.CORRECT -> R.drawable.ic_checkmark
        FeedbackType.WRONG -> R.drawable.ic_cross
    }

    val backgroundColor = when (type) {
        FeedbackType.CORRECT -> Color(0xFF4CAF50).copy(alpha = 0.9f)
        FeedbackType.WRONG -> Color(0xFFF44336).copy(alpha = 0.9f)
    }

    LaunchedEffect(visible) {
        if (visible && onAnimationComplete != null) {
            delay(1000)
            onAnimationComplete()
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(),
        exit = scaleOut() + fadeOut(),
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = if (showBackground) {
                Modifier
                    .size(size + 16.dp)
                    .background(backgroundColor, CircleShape)
                    .padding(8.dp)
            } else {
                Modifier
            }
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = if (type == FeedbackType.CORRECT) "Correct" else "Wrong",
                modifier = Modifier.size(size)
            )
        }
    }
}

/**
 * Animated feedback overlay that appears briefly
 */
@Composable
fun FeedbackOverlay(
    isCorrect: Boolean?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showFeedback by remember(isCorrect) { mutableStateOf(isCorrect != null) }

    LaunchedEffect(isCorrect) {
        if (isCorrect != null) {
            showFeedback = true
            delay(800)
            showFeedback = false
            delay(300)
            onDismiss()
        }
    }

    if (isCorrect != null) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            FeedbackIcon(
                type = if (isCorrect) FeedbackType.CORRECT else FeedbackType.WRONG,
                visible = showFeedback,
                size = 80.dp
            )
        }
    }
}

/**
 * Touch hint icon for game tutorials
 */
@Composable
fun TouchHint(
    type: TouchHintType,
    visible: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    val iconRes = when (type) {
        TouchHintType.TAP -> R.drawable.ic_touch_tap
        TouchHintType.HOLD -> R.drawable.ic_touch_hold
        TouchHintType.SWIPE_LEFT -> R.drawable.ic_touch_swipe_left
        TouchHintType.SWIPE_RIGHT -> R.drawable.ic_touch_swipe_right
        TouchHintType.SWIPE_UP -> R.drawable.ic_touch_swipe_up
        TouchHintType.SWIPE_DOWN -> R.drawable.ic_touch_swipe_down
        TouchHintType.DRAG -> R.drawable.ic_touch_drag
    }

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "touchHintScale"
    )

    if (scale > 0.01f) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = "Touch hint",
            modifier = modifier
                .size(size)
                .scale(scale)
        )
    }
}

enum class TouchHintType {
    TAP,
    HOLD,
    SWIPE_LEFT,
    SWIPE_RIGHT,
    SWIPE_UP,
    SWIPE_DOWN,
    DRAG
}
