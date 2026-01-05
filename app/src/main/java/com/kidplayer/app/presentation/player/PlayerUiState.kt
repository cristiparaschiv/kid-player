package com.kidplayer.app.presentation.player

import com.kidplayer.app.domain.model.MediaItem

/**
 * UI State for the Player screen
 * Manages playback state, progress, and errors
 */
data class PlayerUiState(
    val mediaItem: MediaItem? = null,
    val streamingUrl: String? = null,
    val isLoading: Boolean = true,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val currentPosition: Long = 0L, // in milliseconds
    val duration: Long = 0L, // in milliseconds
    val error: String? = null,
    val playerReady: Boolean = false,
    val autoplayCountdown: Int = 0, // seconds until next video plays
    val nextMediaItem: MediaItem? = null,
    val isOfflinePlayback: Boolean = false, // true if playing from local storage
    val isNetworkOffline: Boolean = false, // true if device has no network connection
    val screenTimeRemaining: Int? = null, // remaining minutes (null if disabled)
    val isTimeLimitReached: Boolean = false, // true if time limit reached
    val recommendedVideos: List<MediaItem> = emptyList(), // videos to show in recommendation row
    val timeLimitDailyMinutes: Int = 0, // daily limit in minutes for displaying in overlay
    val timeLimitUsedMinutes: Int = 0, // used minutes for displaying in overlay
    val pinVerificationError: String? = null, // error message when PIN verification fails
    val resumePositionMs: Long = 0L, // position to resume from (synced from Jellyfin)
    val hasResumedPlayback: Boolean = false // tracks if we've already seeked to resume position
) {
    /**
     * Returns true if the player has an error
     */
    fun hasError(): Boolean = error != null

    /**
     * Returns playback progress as a percentage (0.0 to 1.0)
     */
    fun getProgress(): Float {
        return if (duration > 0) {
            (currentPosition.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
    }

    /**
     * Returns formatted current position as MM:SS or HH:MM:SS
     */
    fun getFormattedPosition(): String {
        return formatMilliseconds(currentPosition)
    }

    /**
     * Returns formatted duration as MM:SS or HH:MM:SS
     */
    fun getFormattedDuration(): String {
        return formatMilliseconds(duration)
    }

    /**
     * Returns true if video has been watched past 90%
     */
    fun isWatched(): Boolean {
        return getProgress() >= 0.9f
    }

    /**
     * Returns remaining time in milliseconds
     */
    fun getRemainingTime(): Long {
        return (duration - currentPosition).coerceAtLeast(0L)
    }

    private fun formatMilliseconds(millis: Long): String {
        val totalSeconds = millis / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%d:%02d", minutes, seconds)
        }
    }
}
