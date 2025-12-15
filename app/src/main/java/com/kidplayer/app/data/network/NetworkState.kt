package com.kidplayer.app.data.network

/**
 * Represents the current network connectivity state
 */
enum class NetworkState {
    /**
     * Device is connected to the internet (any type)
     */
    ONLINE,

    /**
     * Device has no internet connection
     */
    OFFLINE,

    /**
     * Device is connected via WiFi
     */
    WIFI,

    /**
     * Device is connected via cellular/mobile data
     */
    CELLULAR
}

/**
 * Sealed class representing the result of a network-dependent operation
 */
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val message: String, val isNetworkError: Boolean = false) : NetworkResult<Nothing>()
    data object Loading : NetworkResult<Nothing>()
    data object Offline : NetworkResult<Nothing>()
}
