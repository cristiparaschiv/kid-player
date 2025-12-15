package com.kidplayer.app.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kidplayer.app.presentation.components.rememberHapticFeedback
import com.kidplayer.app.ui.theme.Dimensions

/**
 * Kid-friendly error states for different error scenarios
 */
enum class ErrorType {
    NETWORK_ERROR,
    NO_CONTENT,
    GENERIC_ERROR,
    OFFLINE,
    SERVER_ERROR
}

/**
 * Full screen error display with kid-friendly messages and retry button
 */
@Composable
fun ErrorState(
    errorType: ErrorType,
    message: String? = null,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val (icon, title, description) = getErrorContent(errorType, message)
    val haptic = rememberHapticFeedback()

    Box(
        modifier = modifier
            .fillMaxSize()
            .semantics {
                liveRegion = LiveRegionMode.Assertive
                contentDescription = "Error: $title. $description"
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimensions.spacingL),
            modifier = Modifier
                .padding(Dimensions.paddingXxl)
                .widthIn(max = 500.dp)
        ) {
            // Error icon
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.error
            )

            // Error title
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            // Error description
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            // Retry button
            if (onRetry != null) {
                Spacer(modifier = Modifier.height(Dimensions.spacingS))
                Button(
                    onClick = {
                        haptic.performMedium()
                        onRetry()
                    },
                    modifier = Modifier
                        .height(Dimensions.touchTargetRecommended)
                        .widthIn(min = 200.dp)
                        .semantics {
                            contentDescription = "Try again button"
                        },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(Dimensions.iconMedium)
                    )
                    Spacer(modifier = Modifier.width(Dimensions.spacingS))
                    Text(
                        text = "Try Again",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

/**
 * Compact error display for inline errors
 */
@Composable
fun CompactErrorState(
    message: String,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val haptic = rememberHapticFeedback()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimensions.paddingL)
            .semantics {
                liveRegion = LiveRegionMode.Polite
                contentDescription = "Error: $message"
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.paddingL),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimensions.spacingM)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingS),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }

            if (onRetry != null) {
                TextButton(
                    onClick = {
                        haptic.performLight()
                        onRetry()
                    },
                    modifier = Modifier.semantics {
                        contentDescription = "Try again button"
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(Dimensions.iconSmall)
                    )
                    Spacer(modifier = Modifier.width(Dimensions.paddingXs))
                    Text("Try Again")
                }
            }
        }
    }
}

/**
 * Empty state display (no errors, just no content)
 */
@Composable
fun EmptyState(
    title: String = "No Videos Yet",
    description: String = "Videos will appear here when they're added",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .semantics {
                liveRegion = LiveRegionMode.Polite
                contentDescription = "$title. $description"
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimensions.spacingL),
            modifier = Modifier.padding(Dimensions.paddingXxl)
        ) {
            Icon(
                imageVector = Icons.Default.SentimentDissatisfied,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
            )

            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Get appropriate icon, title, and description for each error type
 */
private fun getErrorContent(
    errorType: ErrorType,
    customMessage: String?
): Triple<ImageVector, String, String> {
    return when (errorType) {
        ErrorType.NETWORK_ERROR -> Triple(
            Icons.Default.WifiOff,
            "Can't Connect",
            customMessage ?: "We're having trouble connecting. Check your internet and try again!"
        )

        ErrorType.OFFLINE -> Triple(
            Icons.Default.CloudOff,
            "You're Offline",
            customMessage ?: "Connect to the internet to watch more videos. Downloaded videos are still available!"
        )

        ErrorType.NO_CONTENT -> Triple(
            Icons.Default.SentimentDissatisfied,
            "No Videos Found",
            customMessage ?: "There are no videos available right now. Try again later!"
        )

        ErrorType.SERVER_ERROR -> Triple(
            Icons.Default.Error,
            "Server Problem",
            customMessage ?: "The video server isn't responding. Please try again in a few moments!"
        )

        ErrorType.GENERIC_ERROR -> Triple(
            Icons.Default.Error,
            "Oops! Something Went Wrong",
            customMessage ?: "We ran into a problem. Don't worry, let's try again!"
        )
    }
}
