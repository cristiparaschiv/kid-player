package com.kidplayer.app.presentation.browse.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kidplayer.app.domain.model.MediaItem
import com.kidplayer.app.presentation.components.rememberHapticFeedback
import com.kidplayer.app.ui.theme.Dimensions

/**
 * Video card component for grid display
 * Kid-friendly design with large touch targets and bright colors
 * WCAG AAA compliant with 56-64dp touch targets
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoCard(
    mediaItem: MediaItem,
    onClick: () -> Unit,
    onDownloadClick: (() -> Unit)? = null,
    showFavorite: Boolean = false,
    isFavorite: Boolean = false,
    onFavoriteClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val haptic = rememberHapticFeedback()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .semantics {
                contentDescription = "Video: ${mediaItem.getDisplayTitle()}, ${mediaItem.getFormattedDuration()}"
            },
        shape = RoundedCornerShape(Dimensions.cardCornerRadius),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.cardElevation),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onClick = {
            haptic.performLight()
            onClick()
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Thumbnail image
            AsyncImage(
                model = mediaItem.thumbnailUrl,
                contentDescription = null, // Handled by card semantics
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Gradient overlay for better text readability
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )

            // Title overlay
            Text(
                text = mediaItem.getDisplayTitle(),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Favorite button (top right)
            if (showFavorite && onFavoriteClick != null) {
                IconButton(
                    onClick = {
                        haptic.performMedium()
                        onFavoriteClick()
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(Dimensions.paddingS)
                        .size(Dimensions.touchTargetRecommended)
                        .background(
                            color = Color.Black.copy(alpha = 0.6f),
                            shape = CircleShape
                        )
                        .semantics {
                            contentDescription = if (isFavorite) {
                                "Remove ${mediaItem.getDisplayTitle()} from favorites"
                            } else {
                                "Add ${mediaItem.getDisplayTitle()} to favorites"
                            }
                        }
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null, // Handled by button semantics
                        tint = if (isFavorite) Color.Red else Color.White,
                        modifier = Modifier.size(Dimensions.iconLarge)
                    )
                }
            } else if (mediaItem.duration > 0) {
                // Duration badge (top right) - only show if not showing favorite
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(Dimensions.paddingS)
                        .semantics {
                            contentDescription = "Duration: ${mediaItem.getFormattedDuration()}"
                        },
                    shape = RoundedCornerShape(Dimensions.chipCornerRadius),
                    color = Color.Black.copy(alpha = 0.7f)
                ) {
                    Text(
                        text = mediaItem.getFormattedDuration(),
                        modifier = Modifier.padding(horizontal = Dimensions.paddingS, vertical = Dimensions.paddingXs),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Watched indicator (top left)
            if (mediaItem.isWatched()) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(Dimensions.paddingS)
                        .semantics {
                            contentDescription = "Already watched"
                        },
                    shape = RoundedCornerShape(Dimensions.chipCornerRadius),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = "WATCHED",
                        modifier = Modifier.padding(horizontal = Dimensions.paddingS, vertical = Dimensions.paddingXs),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else if (mediaItem.watchedPercentage > 0.05f) {
                // Progress indicator
                LinearProgressIndicator(
                    progress = mediaItem.watchedPercentage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dimensions.progressBarHeightMedium)
                        .align(Alignment.BottomCenter)
                        .semantics {
                            contentDescription = "Watched ${(mediaItem.watchedPercentage * 100).toInt()}%"
                        },
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = Color.Transparent,
                )
            }

            // Download indicator/button (bottom right)
            if (onDownloadClick != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(Dimensions.paddingS)
                ) {
                    when {
                        // Downloaded - show checkmark
                        mediaItem.isDownloaded -> {
                            Surface(
                                modifier = Modifier
                                    .size(Dimensions.touchTargetRecommended)
                                    .semantics {
                                        contentDescription = "${mediaItem.getDisplayTitle()} is downloaded"
                                    },
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.secondary
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSecondary,
                                        modifier = Modifier.size(Dimensions.iconLarge)
                                    )
                                }
                            }
                        }
                        // Downloading - show progress circle
                        mediaItem.downloadProgress > 0f && mediaItem.downloadProgress < 1f -> {
                            Box(
                                modifier = Modifier.size(Dimensions.touchTargetRecommended),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    progress = mediaItem.downloadProgress,
                                    modifier = Modifier.size(Dimensions.touchTargetRecommended),
                                    color = MaterialTheme.colorScheme.primary,
                                    strokeWidth = 4.dp
                                )
                                // Cancel button in center
                                IconButton(
                                    onClick = {
                                        haptic.performMedium()
                                        onDownloadClick()
                                    },
                                    modifier = Modifier
                                        .size(Dimensions.touchTargetMin)
                                        .semantics {
                                            contentDescription = "Cancel download of ${mediaItem.getDisplayTitle()}"
                                        }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Cancel,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(Dimensions.iconMedium)
                                    )
                                }
                            }
                        }
                        // Not downloaded - show download button
                        else -> {
                            IconButton(
                                onClick = {
                                    haptic.performMedium()
                                    onDownloadClick()
                                },
                                modifier = Modifier
                                    .size(Dimensions.touchTargetRecommended)
                                    .background(
                                        color = Color.Black.copy(alpha = 0.6f),
                                        shape = CircleShape
                                    )
                                    .semantics {
                                        contentDescription = "Download ${mediaItem.getDisplayTitle()}"
                                    }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(Dimensions.iconLarge)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
