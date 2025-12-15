package com.kidplayer.app.presentation.player.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kidplayer.app.domain.model.MediaItem
import com.kidplayer.app.presentation.components.rememberHapticFeedback

/**
 * Horizontal scrollable video recommendation row for player overlay
 * Optimized for kids ages 4-10 with large touch targets and clear thumbnails
 *
 * Design considerations:
 * - Large thumbnails (160x90dp) for easy recognition and tapping
 * - High contrast text overlay for readability during playback
 * - Horizontal scrolling with momentum for exploration
 * - Haptic feedback on selection
 */
@Composable
fun VideoRecommendationRow(
    recommendations: List<MediaItem>,
    currentVideoId: String,
    onVideoSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = rememberHapticFeedback()

    // Filter out current video from recommendations
    val filteredRecommendations = recommendations.filter { it.id != currentVideoId }

    if (filteredRecommendations.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .semantics {
                contentDescription = "${filteredRecommendations.size} recommended videos"
            }
    ) {
        // Section header
        Text(
            text = "Up Next",
            modifier = Modifier.padding(start = 24.dp, bottom = 8.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 16.sp
        )

        // Horizontal scrollable row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(
                items = filteredRecommendations,
                key = { it.id }
            ) { mediaItem ->
                RecommendationCard(
                    mediaItem = mediaItem,
                    onClick = {
                        haptic.performMedium()
                        onVideoSelect(mediaItem.id)
                    }
                )
            }
        }
    }
}

/**
 * Individual recommendation card with thumbnail and title
 *
 * Touch target: 140x100dp (exceeds 56dp minimum for kids)
 * Aspect ratio: 16:9 for video thumbnails
 */
@Composable
private fun RecommendationCard(
    mediaItem: MediaItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(140.dp)
            .height(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = "Play ${mediaItem.getDisplayTitle()}, ${mediaItem.getFormattedDuration()}"
            }
    ) {
        // Thumbnail
        AsyncImage(
            model = mediaItem.thumbnailUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        // Gradient overlay for text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.8f)
                        ),
                        startY = 30f
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
        )

        // Duration badge (top-right)
        if (mediaItem.duration > 0) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp),
                shape = RoundedCornerShape(4.dp),
                color = Color.Black.copy(alpha = 0.8f)
            ) {
                Text(
                    text = mediaItem.getFormattedDuration(),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }
        }

        // Title overlay (bottom)
        Text(
            text = mediaItem.getDisplayTitle(),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontSize = 11.sp,
            lineHeight = 13.sp
        )
    }
}
