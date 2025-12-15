package com.kidplayer.app.domain.model

/**
 * Represents a Jellyfin library/collection that contains media items
 */
data class Library(
    val id: String,
    val name: String,
    val collectionType: String? = null,
    val isEnabled: Boolean = true
) {
    /**
     * Returns true if this library contains video content
     */
    fun isVideoLibrary(): Boolean {
        return collectionType?.lowercase() in listOf("movies", "tvshows", "mixed")
    }
}
