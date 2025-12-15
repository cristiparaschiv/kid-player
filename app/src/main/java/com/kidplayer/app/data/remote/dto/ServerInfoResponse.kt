package com.kidplayer.app.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Response from Jellyfin system info endpoint
 * Used to verify server connectivity and get server details
 */
data class ServerInfoResponse(
    @SerializedName("Id")
    val id: String,

    @SerializedName("ServerName")
    val serverName: String,

    @SerializedName("Version")
    val version: String,

    @SerializedName("OperatingSystem")
    val operatingSystem: String? = null,

    @SerializedName("LocalAddress")
    val localAddress: String? = null
)
