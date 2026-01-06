package com.kidplayer.app.domain.model

/**
 * Represents a Jellyfin playlist
 * Playlists are user-created collections of media items
 */
data class Playlist(
    val id: String,
    val name: String,
    val itemCount: Int = 0,
    val imageUrl: String? = null
)
