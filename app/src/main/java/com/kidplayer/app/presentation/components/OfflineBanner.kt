package com.kidplayer.app.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.SignalWifi4Bar
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kidplayer.app.data.network.NetworkState

/**
 * Banner that appears at the top of the screen to indicate network status
 * Shows when device is offline or has limited connectivity
 */
@Composable
fun OfflineBanner(
    networkState: NetworkState,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = networkState == NetworkState.OFFLINE,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(durationMillis = 300)
        ) + fadeIn(animationSpec = tween(durationMillis = 300)),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(durationMillis = 300)
        ) + fadeOut(animationSpec = tween(durationMillis = 300))
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.error)
                .padding(vertical = 12.dp, horizontal = 16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.WifiOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onError,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "You're offline - Only downloaded videos can play",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onError
                )
            }
        }
    }
}

/**
 * Compact network status indicator
 * Shows current network type (WiFi/Cellular/Offline)
 */
@Composable
fun NetworkStatusIndicator(
    networkState: NetworkState,
    showWhenOnline: Boolean = false,
    modifier: Modifier = Modifier
) {
    // Define display properties based on network state
    val icon = when (networkState) {
        NetworkState.OFFLINE -> Icons.Default.CloudOff
        else -> Icons.Default.SignalWifi4Bar
    }

    val text = when (networkState) {
        NetworkState.OFFLINE -> "Offline"
        NetworkState.CELLULAR -> "Mobile Data"
        NetworkState.WIFI, NetworkState.ONLINE -> "Connected"
    }

    val backgroundColor = when (networkState) {
        NetworkState.OFFLINE -> MaterialTheme.colorScheme.error
        NetworkState.CELLULAR -> MaterialTheme.colorScheme.tertiaryContainer
        NetworkState.WIFI, NetworkState.ONLINE -> MaterialTheme.colorScheme.primaryContainer
    }

    val textColor = when (networkState) {
        NetworkState.OFFLINE -> MaterialTheme.colorScheme.onError
        NetworkState.CELLULAR -> MaterialTheme.colorScheme.onTertiaryContainer
        NetworkState.WIFI, NetworkState.ONLINE -> MaterialTheme.colorScheme.onPrimaryContainer
    }

    val shouldShow = networkState == NetworkState.OFFLINE ||
                     networkState == NetworkState.CELLULAR ||
                     showWhenOnline

    AnimatedVisibility(
        visible = shouldShow,
        enter = fadeIn() + expandHorizontally(),
        exit = fadeOut() + shrinkHorizontally()
    ) {
        Row(
            modifier = modifier
                .background(
                    color = backgroundColor,
                    shape = MaterialTheme.shapes.small
                )
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
        }
    }
}

/**
 * Full-width banner with detailed offline information
 */
@Composable
fun OfflineModeBanner(
    isVisible: Boolean,
    downloadedCount: Int = 0,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudOff,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(32.dp)
                    )
                    Column {
                        Text(
                            text = "Offline Mode",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (downloadedCount > 0) {
                            Text(
                                text = "$downloadedCount downloaded video${if (downloadedCount > 1) "s" else ""} available",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        } else {
                            Text(
                                text = "No downloaded videos available",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Inline network warning chip
 * Useful for showing network status within cards or smaller UI elements
 */
@Composable
fun NetworkWarningChip(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f),
                shape = MaterialTheme.shapes.small
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.WifiOff,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}
