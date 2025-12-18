package com.kidplayer.app.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Request body for reporting playback started to Jellyfin
 * POST /Sessions/Playing
 */
data class PlaybackStartInfo(
    @SerializedName("ItemId")
    val itemId: String,

    @SerializedName("CanSeek")
    val canSeek: Boolean = true,

    @SerializedName("IsPaused")
    val isPaused: Boolean = false,

    @SerializedName("IsMuted")
    val isMuted: Boolean = false,

    @SerializedName("PositionTicks")
    val positionTicks: Long = 0L
)

/**
 * Request body for reporting playback progress to Jellyfin
 * POST /Sessions/Playing/Progress
 */
data class PlaybackProgressInfo(
    @SerializedName("ItemId")
    val itemId: String,

    @SerializedName("PositionTicks")
    val positionTicks: Long,

    @SerializedName("CanSeek")
    val canSeek: Boolean = true,

    @SerializedName("IsPaused")
    val isPaused: Boolean = false,

    @SerializedName("IsMuted")
    val isMuted: Boolean = false
)

/**
 * Request body for reporting playback stopped to Jellyfin
 * POST /Sessions/Playing/Stopped
 */
data class PlaybackStopInfo(
    @SerializedName("ItemId")
    val itemId: String,

    @SerializedName("PositionTicks")
    val positionTicks: Long
)
