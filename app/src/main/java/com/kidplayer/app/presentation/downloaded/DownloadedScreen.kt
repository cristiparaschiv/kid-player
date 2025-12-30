package com.kidplayer.app.presentation.downloaded

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidplayer.app.domain.model.MediaItem
import com.kidplayer.app.presentation.browse.components.VideoCard
import com.kidplayer.app.presentation.components.KidFriendlyBackgroundWrapper
import com.kidplayer.app.R

/**
 * Downloaded screen showing all downloaded and downloading videos
 * Displays downloaded items and active downloads with progress
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadedScreen(
    onVideoClick: (String) -> Unit,
    viewModel: DownloadedViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    KidFriendlyBackgroundWrapper(
        backgroundImageRes = R.drawable.cartoon_background
    ) {
        Scaffold(
            topBar = {
                DownloadedTopBar(
                    onRefreshClick = { viewModel.refresh() },
                    downloadCount = uiState.getTotalCount()
                )
            },
            containerColor = androidx.compose.ui.graphics.Color.Transparent
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when {
                    uiState.isLoading -> {
                        LoadingState()
                    }
                    uiState.hasError() && uiState.isEmpty() -> {
                        ErrorState(
                            message = uiState.error ?: "Unknown error",
                            onRetry = { viewModel.retry() }
                        )
                    }
                    uiState.isEmpty() -> {
                        EmptyState()
                    }
                    else -> {
                        DownloadedContent(
                            downloadedItems = uiState.downloadedItems,
                            downloadingItems = uiState.downloadingItems,
                            onVideoClick = onVideoClick,
                            onCancelDownload = { viewModel.cancelDownload(it) },
                            onDeleteDownload = { viewModel.deleteDownload(it) }
                        )
                    }
                }

                // Error snackbar overlay
                if (uiState.error != null && !uiState.isEmpty()) {
                    Snackbar(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
                        action = {
                            TextButton(onClick = { viewModel.dismissError() }) {
                                Text("OK")
                            }
                        }
                    ) {
                        Text(uiState.error ?: "")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadedTopBar(
    onRefreshClick: () -> Unit,
    downloadCount: Int
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = stringResource(R.string.downloads_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                if (downloadCount > 0) {
                    Text(
                        text = "$downloadCount video${if (downloadCount > 1) "s" else ""}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onRefreshClick) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@Composable
fun DownloadedContent(
    downloadedItems: List<MediaItem>,
    downloadingItems: List<MediaItem>,
    onVideoClick: (String) -> Unit,
    onCancelDownload: (String) -> Unit,
    onDeleteDownload: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 300.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Active downloads section
        if (downloadingItems.isNotEmpty()) {
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                SectionHeader(title = "Downloading (${downloadingItems.size})")
            }

            items(
                items = downloadingItems,
                key = { "downloading_${it.id}" }
            ) { mediaItem ->
                VideoCard(
                    mediaItem = mediaItem,
                    onClick = { /* Don't allow playback while downloading */ },
                    onDownloadClick = { onCancelDownload(mediaItem.id) },
                    showFavorite = false
                )
            }
        }

        // Downloaded section
        if (downloadedItems.isNotEmpty()) {
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                SectionHeader(
                    title = if (downloadingItems.isNotEmpty()) {
                        "Downloaded (${downloadedItems.size})"
                    } else {
                        "All Downloads (${downloadedItems.size})"
                    }
                )
            }

            items(
                items = downloadedItems,
                key = { "downloaded_${it.id}" }
            ) { mediaItem ->
                VideoCard(
                    mediaItem = mediaItem,
                    onClick = { onVideoClick(mediaItem.id) },
                    onDownloadClick = { onDeleteDownload(mediaItem.id) },
                    showFavorite = false
                )
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(64.dp),
                strokeWidth = 6.dp
            )
            Text(
                text = stringResource(R.string.downloads_loading),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CloudDownload,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = "Oops!",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onRetry,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Try Again")
            }
        }
    }
}

@Composable
fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CloudDownload,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.downloads_empty),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = stringResource(R.string.downloads_empty_desc),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}
