package com.kidplayer.app.presentation.player.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kidplayer.app.presentation.theme.KidPlayerTheme

/**
 * Circular time remaining indicator for video playback
 * Shows remaining screen time in a visual, kid-friendly circular progress format
 *
 * Color-coded:
 * - Green: > 15 minutes remaining
 * - Orange: 6-15 minutes remaining
 * - Red: <= 5 minutes remaining
 */
@Composable
fun CircularTimeIndicator(
    isVisible: Boolean,
    remainingMinutes: Int,
    totalMinutes: Int,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    strokeWidth: Dp = 8.dp
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + scaleIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ),
        exit = fadeOut() + scaleOut(),
        modifier = modifier
    ) {
        // Calculate progress (0 to 1)
        val progress = if (totalMinutes > 0) {
            (remainingMinutes.toFloat() / totalMinutes.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }

        // Animate progress changes with spring animation
        val animatedProgress by animateFloatAsState(
            targetValue = progress,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            label = "progress_animation"
        )

        // Color based on time remaining
        val progressColor = when {
            remainingMinutes <= 5 -> Color(0xFFE53935) // Red
            remainingMinutes <= 15 -> Color(0xFFFF9800) // Orange
            else -> Color(0xFF4CAF50) // Green
        }

        val backgroundColor = when {
            remainingMinutes <= 5 -> Color(0xFFFFCDD2) // Light red
            remainingMinutes <= 15 -> Color(0xFFFFE0B2) // Light orange
            else -> Color(0xFFC8E6C9) // Light green
        }

        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(backgroundColor.copy(alpha = 0.9f)),
            contentAlignment = Alignment.Center
        ) {
            // Background track
            Canvas(modifier = Modifier.fillMaxSize().padding(strokeWidth / 2)) {
                drawArc(
                    color = progressColor.copy(alpha = 0.2f),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(
                        width = strokeWidth.toPx(),
                        cap = StrokeCap.Round
                    )
                )
            }

            // Progress arc
            Canvas(modifier = Modifier.fillMaxSize().padding(strokeWidth / 2)) {
                drawArc(
                    color = progressColor,
                    startAngle = -90f,
                    sweepAngle = animatedProgress * 360f,
                    useCenter = false,
                    style = Stroke(
                        width = strokeWidth.toPx(),
                        cap = StrokeCap.Round
                    )
                )
            }

            // Center text showing minutes
            Text(
                text = formatCenterText(remainingMinutes),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = progressColor
            )
        }
    }
}

/**
 * Format the center text display
 */
private fun formatCenterText(minutes: Int): String {
    return when {
        minutes <= 0 -> "0"
        minutes >= 60 -> {
            val hours = minutes / 60
            val mins = minutes % 60
            if (mins == 0) "${hours}h" else "${hours}h"
        }
        else -> "$minutes"
    }
}

/**
 * Compact version for smaller displays
 */
@Composable
fun CompactCircularTimeIndicator(
    isVisible: Boolean,
    remainingMinutes: Int,
    totalMinutes: Int,
    modifier: Modifier = Modifier
) {
    CircularTimeIndicator(
        isVisible = isVisible,
        remainingMinutes = remainingMinutes,
        totalMinutes = totalMinutes,
        modifier = modifier,
        size = 56.dp,
        strokeWidth = 6.dp
    )
}

@Preview(name = "Circular Time - Full")
@Composable
private fun CircularTimeIndicatorFullPreview() {
    KidPlayerTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularTimeIndicator(
                isVisible = true,
                remainingMinutes = 45,
                totalMinutes = 60
            )
        }
    }
}

@Preview(name = "Circular Time - Warning")
@Composable
private fun CircularTimeIndicatorWarningPreview() {
    KidPlayerTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularTimeIndicator(
                isVisible = true,
                remainingMinutes = 10,
                totalMinutes = 60
            )
        }
    }
}

@Preview(name = "Circular Time - Critical")
@Composable
private fun CircularTimeIndicatorCriticalPreview() {
    KidPlayerTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularTimeIndicator(
                isVisible = true,
                remainingMinutes = 3,
                totalMinutes = 60
            )
        }
    }
}
