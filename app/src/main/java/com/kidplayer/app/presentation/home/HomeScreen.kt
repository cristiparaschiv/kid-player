package com.kidplayer.app.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayCircle
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidplayer.app.domain.model.ContinueWatchingItem
import com.kidplayer.app.domain.model.MediaItem
import com.kidplayer.app.presentation.browse.components.VideoCard
import com.kidplayer.app.presentation.components.ErrorState
import com.kidplayer.app.presentation.components.ErrorType
import com.kidplayer.app.presentation.components.OfflineBanner
import com.kidplayer.app.presentation.components.VideoGridShimmer
import com.kidplayer.app.presentation.components.rememberHapticFeedback
import com.kidplayer.app.presentation.home.components.ContinueWatchingCard
import com.kidplayer.app.presentation.components.ExtraSubtleBackgroundWrapper
import com.kidplayer.app.ui.theme.Dimensions
import com.kidplayer.app.R
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

/**
 * Home screen with ALL content
 * Phase 6: Updated to show all videos in grid like Browse screen
 * Optimized for children ages 4-10 with large touch targets and tablet support
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onVideoClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onSeeAllClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val networkState by viewModel.networkState.collectAsState()
    val haptic = rememberHapticFeedback()

    ExtraSubtleBackgroundWrapper(
        backgroundImageRes = R.drawable.cartoon_background
    ) {
        Scaffold(
            topBar = {
                HomeTopBar(
                    onSettingsClick = {
                        haptic.performMedium()
                        onSettingsClick()
                    },
                    onRefreshClick = {
                        haptic.performLight()
                        viewModel.refresh()
                    }
                )
            },
            containerColor = androidx.compose.ui.graphics.Color.Transparent
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
                    !uiState.isServerConfigured -> {
                        // No server configured - show parent setup banner
                        ParentSetupBanner(
                            onSetupClick = {
                                haptic.performMedium()
                                onSettingsClick()
                            }
                        )
                    }
                    uiState.isLoading -> {
                        // Loading state with shimmer effect
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .semantics {
                                    liveRegion = LiveRegionMode.Polite
                                    contentDescription = "Loading videos, please wait"
                                }
                        ) {
                            VideoGridShimmer(columns = 3, itemCount = 9)
                        }
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
                            onRetry = {
                                haptic.performLight()
                                viewModel.retry()
                            }
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
                                    onLibrarySelected = {
                                        haptic.performLight()
                                        viewModel.selectLibrary(it)
                                    }
                                )
                            }

                            // Video grid with pagination
                            VideoGrid(
                                mediaItems = uiState.mediaItems,
                                isLoadingMore = uiState.isLoadingMore,
                                hasMoreItems = uiState.hasMoreItems,
                                totalItemCount = uiState.totalItemCount,
                                favoriteIds = uiState.favoriteIds,
                                onVideoClick = onVideoClick,
                                onDownloadClick = { mediaItemId ->
                                    viewModel.onDownloadClick(mediaItemId)
                                },
                                onFavoriteClick = { mediaItemId ->
                                    viewModel.toggleFavorite(mediaItemId)
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
                            .padding(Dimensions.paddingL)
                            .semantics {
                                liveRegion = LiveRegionMode.Polite
                                contentDescription = "Error: ${uiState.error}"
                            },
                        shape = RoundedCornerShape(Dimensions.chipCornerRadius),
                        color = MaterialTheme.colorScheme.errorContainer,
                        tonalElevation = 6.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(Dimensions.paddingL),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = uiState.error ?: "",
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            TextButton(
                                onClick = {
                                    haptic.performLight()
                                    viewModel.dismissError()
                                }
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    onSettingsClick: () -> Unit,
    onRefreshClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.home_all_videos),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        },
        actions = {
            IconButton(
                onClick = onRefreshClick,
                modifier = Modifier
                    .size(Dimensions.touchTargetMin)
                    .semantics {
                        contentDescription = "Refresh videos"
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(Dimensions.iconLarge)
                )
            }
            // Settings button with Lock icon and border to indicate parent-only
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .size(Dimensions.touchTargetMin)
                    .padding(Dimensions.paddingXs)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.error,
                        shape = CircleShape
                    )
                    .semantics {
                        contentDescription = "Parent settings (locked)"
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(Dimensions.iconLarge)
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
        edgePadding = Dimensions.paddingL
    ) {
        libraries.forEach { library ->
            Tab(
                selected = library.id == selectedLibraryId,
                onClick = { onLibrarySelected(library.id) },
                modifier = Modifier
                    .height(Dimensions.touchTargetMin)
                    .semantics {
                        contentDescription = if (library.id == selectedLibraryId) {
                            "${library.name} library, selected"
                        } else {
                            "${library.name} library"
                        }
                    },
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
    mediaItems: List<MediaItem>,
    isLoadingMore: Boolean,
    hasMoreItems: Boolean,
    totalItemCount: Int,
    favoriteIds: Set<String>,
    onVideoClick: (String) -> Unit,
    onDownloadClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyGridState()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // Determine grid columns and spacing based on screen width
    // Tablets: 3 columns for better screen utilization
    // Phones: 2 columns for larger touch targets on smaller screens
    val isTablet = screenWidth >= Dimensions.tabletMinWidth
    val gridColumns = if (isTablet) 3 else 2
    val gridSpacing = if (isTablet) Dimensions.gridSpacingTablet else Dimensions.gridSpacingPhone

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
        columns = GridCells.Fixed(gridColumns),
        contentPadding = PaddingValues(gridSpacing),
        horizontalArrangement = Arrangement.spacedBy(gridSpacing),
        verticalArrangement = Arrangement.spacedBy(gridSpacing),
        state = listState,
        modifier = modifier
            .semantics {
                contentDescription = "${mediaItems.size} videos available"
            }
    ) {
        items(
            items = mediaItems,
            key = { it.id }
        ) { mediaItem ->
            VideoCard(
                mediaItem = mediaItem,
                onClick = { onVideoClick(mediaItem.id) },
                onDownloadClick = { onDownloadClick(mediaItem.id) },
                showFavorite = true,
                isFavorite = mediaItem.id in favoriteIds,
                onFavoriteClick = { onFavoriteClick(mediaItem.id) }
            )
        }

        // Loading indicator at the bottom
        if (isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimensions.paddingL)
                        .semantics {
                            liveRegion = LiveRegionMode.Polite
                            contentDescription = "Loading more videos"
                        },
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
                        .padding(vertical = Dimensions.paddingL)
                        .semantics {
                            contentDescription = if (hasMoreItems) {
                                "Showing ${mediaItems.size} of $totalItemCount videos"
                            } else {
                                "All ${mediaItems.size} videos loaded"
                            }
                        },
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

/**
 * Banner shown when no Jellyfin server is configured
 * Kid-friendly design that prompts parents to set up the connection
 * Responsive layout for both portrait and landscape orientations
 */
@Composable
fun ParentSetupBanner(
    onSetupClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isCompactHeight = configuration.screenHeightDp < 480

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(if (isCompactHeight) Dimensions.paddingM else Dimensions.paddingXl)
                .widthIn(max = if (isCompactHeight) 600.dp else 500.dp),
            shape = RoundedCornerShape(if (isCompactHeight) 16.dp else 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            if (isCompactHeight) {
                // Horizontal layout for landscape phones
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Video icon
                    Icon(
                        imageVector = Icons.Default.PlayCircle,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    // Text content
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.home_want_videos),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = stringResource(R.string.home_ask_grownup),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }

                    // Parent setup button
                    Button(
                        onClick = onSetupClick,
                        modifier = Modifier.semantics {
                            contentDescription = "Parent setup button - requires PIN"
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.home_setup),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                // Vertical layout for portrait/tablets
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Video icon
                    Icon(
                        imageVector = Icons.Default.PlayCircle,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    // Title
                    Text(
                        text = stringResource(R.string.home_want_videos),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    // Description
                    Text(
                        text = stringResource(R.string.home_ask_grownup_long),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Parent setup button
                    Button(
                        onClick = onSetupClick,
                        modifier = Modifier
                            .height(Dimensions.touchTargetMin)
                            .semantics {
                                contentDescription = "Parent setup button - requires PIN"
                            },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.home_parent_setup),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Hint text
                    Text(
                        text = stringResource(R.string.home_meanwhile_games),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
