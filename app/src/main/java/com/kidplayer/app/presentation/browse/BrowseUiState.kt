package com.kidplayer.app.presentation.browse

import com.kidplayer.app.domain.model.Library
import com.kidplayer.app.domain.model.MediaItem

/**
 * UI state for the Browse screen
 */
data class BrowseUiState(
    val libraries: List<Library> = emptyList(),
    val selectedLibraryId: String? = null,
    val mediaItems: List<MediaItem> = emptyList(),
    val favoriteIds: Set<String> = emptySet(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    // Pagination state
    val totalItemCount: Int = 0,
    val hasMoreItems: Boolean = false,
    val currentPage: Int = 0
) {
    /**
     * Get the currently selected library
     */
    fun getSelectedLibrary(): Library? {
        return libraries.find { it.id == selectedLibraryId }
    }

    /**
     * Check if there are no media items to display
     */
    fun isEmpty(): Boolean {
        return !isLoading && mediaItems.isEmpty()
    }

    /**
     * Check if there is an error and no cached data
     */
    fun hasError(): Boolean {
        return !isLoading && error != null && mediaItems.isEmpty()
    }

    /**
     * Check if we can load more items
     */
    fun canLoadMore(): Boolean {
        return hasMoreItems && !isLoading && !isLoadingMore
    }
}
