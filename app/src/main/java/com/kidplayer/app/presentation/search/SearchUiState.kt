package com.kidplayer.app.presentation.search

import com.kidplayer.app.domain.model.MediaItem

/**
 * UI state for Search screen
 */
data class SearchUiState(
    val query: String = "",
    val searchResults: List<MediaItem> = emptyList(),
    val searchHistory: List<String> = emptyList(),
    val isSearching: Boolean = false,
    val hasSearched: Boolean = false,
    val error: String? = null
) {
    fun isEmpty(): Boolean {
        return searchResults.isEmpty() && hasSearched
    }

    fun showHistory(): Boolean {
        return query.isEmpty() && !hasSearched && searchHistory.isNotEmpty()
    }
}
