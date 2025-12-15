package com.kidplayer.app.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Monitors network connectivity state in real-time
 * Provides both StateFlow and callback-based network state updates
 */
@Singleton
class NetworkMonitor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _networkState = MutableStateFlow(getCurrentNetworkState())
    val networkState: StateFlow<NetworkState> = _networkState.asStateFlow()

    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private var isMonitoring = false

    /**
     * Gets the current network state synchronously
     */
    private fun getCurrentNetworkState(): NetworkState {
        val activeNetwork = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)

        return when {
            capabilities == null -> NetworkState.OFFLINE
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkState.WIFI
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkState.CELLULAR
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) -> NetworkState.ONLINE
            else -> NetworkState.OFFLINE
        }
    }

    /**
     * Starts monitoring network state changes
     * Should be called when the app comes to foreground
     */
    fun startMonitoring() {
        if (isMonitoring) {
            Timber.d("NetworkMonitor already monitoring")
            return
        }

        Timber.d("Starting network monitoring")

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Timber.d("Network available: $network")
                updateNetworkState()
            }

            override fun onLost(network: Network) {
                Timber.d("Network lost: $network")
                _networkState.value = NetworkState.OFFLINE
            }

            override fun onCapabilitiesChanged(
                network: Network,
                capabilities: NetworkCapabilities
            ) {
                Timber.d("Network capabilities changed: $network")
                updateNetworkState()
            }

            override fun onUnavailable() {
                Timber.d("Network unavailable")
                _networkState.value = NetworkState.OFFLINE
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .build()

        try {
            connectivityManager.registerNetworkCallback(request, networkCallback!!)
            isMonitoring = true
            // Update initial state
            _networkState.value = getCurrentNetworkState()
        } catch (e: Exception) {
            Timber.e(e, "Failed to register network callback")
        }
    }

    /**
     * Stops monitoring network state changes
     * Should be called when the app goes to background
     */
    fun stopMonitoring() {
        if (!isMonitoring) {
            Timber.d("NetworkMonitor not monitoring")
            return
        }

        Timber.d("Stopping network monitoring")

        try {
            networkCallback?.let {
                connectivityManager.unregisterNetworkCallback(it)
            }
            networkCallback = null
            isMonitoring = false
        } catch (e: Exception) {
            Timber.e(e, "Failed to unregister network callback")
        }
    }

    /**
     * Updates the network state based on current capabilities
     */
    private fun updateNetworkState() {
        _networkState.value = getCurrentNetworkState()
    }

    /**
     * Checks if the device is currently online
     */
    fun isOnline(): Boolean {
        val state = _networkState.value
        return state == NetworkState.ONLINE ||
               state == NetworkState.WIFI ||
               state == NetworkState.CELLULAR
    }

    /**
     * Checks if the device is connected via WiFi
     */
    fun isWifi(): Boolean {
        return _networkState.value == NetworkState.WIFI
    }

    /**
     * Checks if the device is connected via cellular
     */
    fun isCellular(): Boolean {
        return _networkState.value == NetworkState.CELLULAR
    }

    /**
     * Flow-based API for observing network connectivity
     * Useful for collecting network state in ViewModels
     */
    fun observeNetworkState(): Flow<NetworkState> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(getCurrentNetworkState())
            }

            override fun onLost(network: Network) {
                trySend(NetworkState.OFFLINE)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                capabilities: NetworkCapabilities
            ) {
                trySend(getCurrentNetworkState())
            }

            override fun onUnavailable() {
                trySend(NetworkState.OFFLINE)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)

        // Emit current state immediately
        trySend(getCurrentNetworkState())

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()
}
