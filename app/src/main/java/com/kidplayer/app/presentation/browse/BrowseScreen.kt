package com.kidplayer.app.presentation.browse

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidplayer.app.presentation.browse.components.VideoCard
import com.kidplayer.app.presentation.components.ErrorState
import com.kidplayer.app.presentation.components.ErrorType
import com.kidplayer.app.presentation.components.FullScreenLoading
import com.kidplayer.app.presentation.components.OfflineBanner
import com.kidplayer.app.presentation.components.VideoGridShimmer
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

/**
 * Browse screen that displays video content in a kid-friendly grid
 * Supports multiple libraries and pull-to-refresh
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseScreen(
    onVideoClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: BrowseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val networkState by viewModel.networkState.collectAsState()

    Scaffold(
        topBar = {
            BrowseTopBar(
                onSettingsClick = onSettingsClick,
                onRefreshClick = { viewModel.onRefresh() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Network status banner at the top
            OfflineBanner(networkState = networkState)

            // Main content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when {
                    uiState.isLoading -> {
                        // Loading state with shimmer effect
                        VideoGridShimmer(columns = 3, itemCount = 9)
                    }
                    uiState.hasError() && uiState.mediaItems.isEmpty() -> {
                        // Error state with retry (only if no cached content)
                        val errorType = when {
                            uiState.error?.contains("offline", ignoreCase = true) == true ||
                            uiState.error?.contains("internet", ignoreCase = true) == true -> ErrorType.OFFLINE
                            uiState.error?.contains("connect", ignoreCase = true) == true ||
                            uiState.error?.contains("network", ignoreCase = true) == true -> ErrorType.NETWORK_ERROR
                            else -> ErrorType.GENERIC_ERROR
                        }
                        ErrorState(
                            errorType = errorType,
                            message = uiState.error,
                            onRetry = { viewModel.retry() }
                        )
                    }
                    uiState.isEmpty() -> {
                        // Empty state
                        com.kidplayer.app.presentation.components.EmptyState(
                            title = "No Videos Yet",
                            description = "Videos will appear here when they're added to your library"
                        )
                    }
                    else -> {
                        // Content with library tabs and grid
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Library tabs if multiple libraries
                            if (uiState.libraries.size > 1) {
                                LibraryTabs(
                                    libraries = uiState.libraries,
                                    selectedLibraryId = uiState.selectedLibraryId,
                                    onLibrarySelected = { viewModel.selectLibrary(it) }
                                )
                            }

                            // Video grid with pagination
                            VideoGrid(
                                mediaItems = uiState.mediaItems,
                                isLoadingMore = uiState.isLoadingMore,
                                hasMoreItems = uiState.hasMoreItems,
                                totalItemCount = uiState.totalItemCount,
                                onVideoClick = onVideoClick,
                                onDownloadClick = { mediaItemId ->
                                    viewModel.onDownloadClick(mediaItemId)
                                },
                                onLoadMore = { viewModel.loadMoreItems() },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Error snackbar overlay (doesn't block content if there are cached items)
                if (uiState.error != null && uiState.mediaItems.isNotEmpty()) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.errorContainer,
                        tonalElevation = 6.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = uiState.error ?: "",
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            TextButton(
                                onClick = { viewModel.dismissError() }
                            ) {
                                Text("OK")
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseTopBar(
    onSettingsClick: () -> Unit,
    onRefreshClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Videos",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        },
        actions = {
            IconButton(onClick = onRefreshClick) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh"
                )
            }
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
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
fun LibraryTabs(
    libraries: List<com.kidplayer.app.domain.model.Library>,
    selectedLibraryId: String?,
    onLibrarySelected: (String) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = libraries.indexOfFirst { it.id == selectedLibraryId }.coerceAtLeast(0),
        containerColor = MaterialTheme.colorScheme.surface,
        edgePadding = 16.dp
    ) {
        libraries.forEach { library ->
            Tab(
                selected = library.id == selectedLibraryId,
                onClick = { onLibrarySelected(library.id) },
                text = {
                    Text(
                        text = library.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (library.id == selectedLibraryId) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        }
                    )
                }
            )
        }
    }
}

@Composable
fun VideoGrid(
    mediaItems: List<com.kidplayer.app.domain.model.MediaItem>,
    isLoadingMore: Boolean,
    hasMoreItems: Boolean,
    totalItemCount: Int,
    onVideoClick: (String) -> Unit,
    onDownloadClick: (String) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyGridState()

    // Infinite scroll logic
    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val totalItemsCount = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            // Trigger load more when user is 10 items away from the end
            lastVisibleItemIndex >= totalItemsCount - 10
        }
            .distinctUntilChanged()
            .filter { it && hasMoreItems && !isLoadingMore }
            .collect {
                onLoadMore()
            }
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 300.dp), // Responsive grid
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        state = listState,
        modifier = modifier
    ) {
        items(
            items = mediaItems,
            key = { it.id }
        ) { mediaItem ->
            VideoCard(
                mediaItem = mediaItem,
                onClick = { onVideoClick(mediaItem.id) },
                onDownloadClick = { onDownloadClick(mediaItem.id) }
            )
        }

        // Loading indicator at the bottom
        if (isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        // Info footer showing item count
        if (mediaItems.isNotEmpty() && !isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (hasMoreItems) {
                            "Showing ${mediaItems.size} of $totalItemCount videos"
                        } else {
                            "All ${mediaItems.size} videos loaded"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
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
                text = "Loading videos...",
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
                modifier = Modifier
                    .padding(top = 16.dp)
                    .height(56.dp)
                    .widthIn(min = 200.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = "Try Again",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
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
            Text(
                text = "No Videos Found",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Ask a grown-up to add some videos!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}
