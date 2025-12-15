package com.kidplayer.app.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Response from Jellyfin items endpoint
 * Used to fetch media items from libraries
 */
data class ItemsResponse(
    @SerializedName("Items")
    val items: List<MediaItemDto>,

    @SerializedName("TotalRecordCount")
    val totalRecordCount: Int = 0,

    @SerializedName("StartIndex")
    val startIndex: Int = 0
)

/**
 * Represents a media item from Jellyfin
 * This can be a movie, episode, or other media type
 */
data class MediaItemDto(
    @SerializedName("Id")
    val id: String,

    @SerializedName("Name")
    val name: String,

    @SerializedName("Type")
    val type: String,

    @SerializedName("Overview")
    val overview: String? = null,

    @SerializedName("RunTimeTicks")
    val runTimeTicks: Long? = null,

    @SerializedName("ProductionYear")
    val productionYear: Int? = null,

    @SerializedName("IndexNumber")
    val indexNumber: Int? = null, // Episode number

    @SerializedName("ParentIndexNumber")
    val parentIndexNumber: Int? = null, // Season number

    @SerializedName("SeriesName")
    val seriesName: String? = null,

    @SerializedName("SeriesId")
    val seriesId: String? = null,

    @SerializedName("SeasonId")
    val seasonId: String? = null,

    @SerializedName("ImageTags")
    val imageTags: Map<String, String>? = null,

    @SerializedName("BackdropImageTags")
    val backdropImageTags: List<String>? = null,

    @SerializedName("UserData")
    val userData: UserDataDto? = null,

    @SerializedName("MediaType")
    val mediaType: String? = null,

    @SerializedName("VideoType")
    val videoType: String? = null,

    @SerializedName("IsFolder")
    val isFolder: Boolean = false,

    @SerializedName("ParentId")
    val parentId: String? = null
)

/**
 * User-specific data for a media item
 */
data class UserDataDto(
    @SerializedName("PlaybackPositionTicks")
    val playbackPositionTicks: Long? = null,

    @SerializedName("PlayCount")
    val playCount: Int = 0,

    @SerializedName("IsFavorite")
    val isFavorite: Boolean = false,

    @SerializedName("Played")
    val played: Boolean = false,

    @SerializedName("Key")
    val key: String? = null
)
