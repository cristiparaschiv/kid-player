package com.kidplayer.app.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Response from Jellyfin authentication endpoint
 */
data class AuthResponse(
    @SerializedName("AccessToken")
    val accessToken: String,

    @SerializedName("User")
    val user: UserDto,

    @SerializedName("ServerId")
    val serverId: String? = null
)

/**
 * User information returned in auth response
 */
data class UserDto(
    @SerializedName("Id")
    val id: String,

    @SerializedName("Name")
    val name: String,

    @SerializedName("ServerId")
    val serverId: String? = null,

    @SerializedName("Policy")
    val policy: UserPolicyDto? = null
)

/**
 * User policy information
 */
data class UserPolicyDto(
    @SerializedName("IsAdministrator")
    val isAdministrator: Boolean = false,

    @SerializedName("IsDisabled")
    val isDisabled: Boolean = false
)
