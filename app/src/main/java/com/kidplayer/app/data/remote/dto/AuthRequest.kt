package com.kidplayer.app.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Request body for Jellyfin authentication
 * Jellyfin uses Pascal case for field names in API
 */
data class AuthRequest(
    @SerializedName("Username")
    val username: String,

    @SerializedName("Pw")
    val password: String
)
