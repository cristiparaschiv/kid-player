package com.kidplayer.app.presentation.home

import com.kidplayer.app.domain.model.ContinueWatchingItem
import com.kidplayer.app.domain.model.Library
import com.kidplayer.app.domain.model.MediaItem

/**
 * UI state for Home screen
 * Phase 6: Updated to show ALL content with pagination like Browse screen
 */
data class HomeUiState(
    // All media items displayed in grid
    val mediaItems: List<MediaItem> = emptyList(),

    // Libraries for filtering
    val libraries: List<Library> = emptyList(),
    val selectedLibraryId: String? = null,

    // Favorites
    val favoriteIds: Set<String> = emptySet(),

    // Pagination state
    val totalItemCount: Int = 0,
    val hasMoreItems: Boolean = false,
    val currentPage: Int = 0,
    val isLoadingMore: Boolean = false,

    // Loading states
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
) {
    fun isEmpty(): Boolean {
        return mediaItems.isEmpty() && !isLoading
    }

    fun hasError(): Boolean = error != null

    fun canLoadMore(): Boolean {
        return hasMoreItems && !isLoading && !isLoadingMore
    }
}
