package com.kidplayer.app.presentation.player.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kidplayer.app.presentation.theme.KidPlayerTheme

/**
 * Time remaining indicator shown during video playback
 * Displays remaining screen time when limit is enabled
 */
@Composable
fun TimeRemainingIndicator(
    isVisible: Boolean,
    remainingMinutes: Int,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(
                    when {
                        remainingMinutes <= 5 -> MaterialTheme.colorScheme.errorContainer
                        remainingMinutes <= 15 -> MaterialTheme.colorScheme.tertiaryContainer
                        else -> MaterialTheme.colorScheme.primaryContainer
                    }
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = "Time remaining",
                    modifier = Modifier.size(24.dp),
                    tint = when {
                        remainingMinutes <= 5 -> MaterialTheme.colorScheme.onErrorContainer
                        remainingMinutes <= 15 -> MaterialTheme.colorScheme.onTertiaryContainer
                        else -> MaterialTheme.colorScheme.onPrimaryContainer
                    }
                )

                Text(
                    text = formatTimeRemaining(remainingMinutes),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        remainingMinutes <= 5 -> MaterialTheme.colorScheme.onErrorContainer
                        remainingMinutes <= 15 -> MaterialTheme.colorScheme.onTertiaryContainer
                        else -> MaterialTheme.colorScheme.onPrimaryContainer
                    }
                )
            }
        }
    }
}

/**
 * Format remaining time in a kid-friendly way
 */
private fun formatTimeRemaining(minutes: Int): String {
    return when {
        minutes <= 0 -> "Time's up!"
        minutes == 1 -> "1 minute left"
        minutes < 60 -> "$minutes minutes left"
        else -> {
            val hours = minutes / 60
            val mins = minutes % 60
            if (mins == 0) {
                if (hours == 1) "1 hour left" else "$hours hours left"
            } else {
                "${hours}h ${mins}m left"
            }
        }
    }
}

@Preview(name = "Time Remaining - Normal")
@Composable
private fun TimeRemainingIndicatorNormalPreview() {
    KidPlayerTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            TimeRemainingIndicator(
                isVisible = true,
                remainingMinutes = 45
            )
        }
    }
}

@Preview(name = "Time Remaining - Warning")
@Composable
private fun TimeRemainingIndicatorWarningPreview() {
    KidPlayerTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            TimeRemainingIndicator(
                isVisible = true,
                remainingMinutes = 10
            )
        }
    }
}

@Preview(name = "Time Remaining - Critical")
@Composable
private fun TimeRemainingIndicatorCriticalPreview() {
    KidPlayerTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            TimeRemainingIndicator(
                isVisible = true,
                remainingMinutes = 3
            )
        }
    }
}
