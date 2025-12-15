package com.kidplayer.app.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Full screen loading indicator with optional message
 * Used for initial data loads
 */
@Composable
fun FullScreenLoading(
    message: String = "Loading videos...",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .semantics {
                liveRegion = LiveRegionMode.Polite
                contentDescription = message
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(64.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 6.dp
            )
            Text(
                text = message,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Grid of video card shimmer placeholders
 * Used while fetching video thumbnails
 */
@Composable
fun VideoGridShimmer(
    columns: Int = 3,
    itemCount: Int = 9,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(itemCount) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VideoCardShimmer()
                TitleShimmer(
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                TextShimmer(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    widthFraction = 0.4f
                )
            }
        }
    }
}

/**
 * Horizontal row of video card shimmer placeholders
 * Used for section-based loading (e.g., "Continue Watching", "Recently Added")
 */
@Composable
fun VideoRowShimmer(
    itemCount: Int = 3,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(itemCount) {
            Column(
                modifier = Modifier.width(280.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VideoCardShimmer()
                TitleShimmer()
                TextShimmer(widthFraction = 0.5f)
            }
        }
    }
}

/**
 * Loading indicator for pagination
 * Shows at the bottom of a list when loading more items
 */
@Composable
fun PaginationLoading(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
            .semantics {
                liveRegion = LiveRegionMode.Polite
                contentDescription = "Loading more videos, please wait"
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp
            )
            Text(
                text = "Loading more videos...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

/**
 * Compact loading indicator
 * Used for smaller UI elements or inline loading
 */
@Composable
fun CompactLoading(
    message: String = "Loading...",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(16.dp)
            .semantics {
                liveRegion = LiveRegionMode.Polite
                contentDescription = message
            },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 3.dp
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
