package com.kidplayer.app.presentation.favorites

import com.kidplayer.app.domain.model.MediaItem

/**
 * UI state for Favorites screen
 */
data class FavoritesUiState(
    val favorites: List<MediaItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) {
    fun isEmpty(): Boolean = favorites.isEmpty() && !isLoading
}
