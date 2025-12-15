package com.kidplayer.app.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Response from Jellyfin library folders endpoint
 */
data class LibrariesResponse(
    @SerializedName("Items")
    val items: List<LibraryDto>,

    @SerializedName("TotalRecordCount")
    val totalRecordCount: Int = 0
)

/**
 * Represents a single library/collection in Jellyfin
 */
data class LibraryDto(
    @SerializedName("Id")
    val id: String,

    @SerializedName("Name")
    val name: String,

    @SerializedName("CollectionType")
    val collectionType: String? = null,

    @SerializedName("ServerId")
    val serverId: String? = null
)
