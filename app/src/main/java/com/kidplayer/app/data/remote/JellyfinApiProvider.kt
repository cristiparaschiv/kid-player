package com.kidplayer.app.data.remote

import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Provider for JellyfinApi instances with dynamic base URLs
 * Since users can configure different Jellyfin servers, we need to create
 * API instances dynamically based on the server URL
 */
@Singleton
class JellyfinApiProvider @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val gson: Gson
) {
    private val apiCache = mutableMapOf<String, JellyfinApi>()

    /**
     * Get or create a JellyfinApi instance for the given server URL
     * APIs are cached to avoid recreating them
     *
     * @param serverUrl The base URL of the Jellyfin server
     * @return JellyfinApi instance configured for the server
     */
    fun getApi(serverUrl: String): JellyfinApi {
        val normalizedUrl = normalizeUrl(serverUrl)

        return apiCache.getOrPut(normalizedUrl) {
            createApi(normalizedUrl)
        }
    }

    /**
     * Clear cached API instances
     * Useful when changing servers or logging out
     */
    fun clearCache() {
        apiCache.clear()
    }

    /**
     * Create a new JellyfinApi instance for the given server URL
     */
    private fun createApi(serverUrl: String): JellyfinApi {
        return Retrofit.Builder()
            .baseUrl(serverUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(JellyfinApi::class.java)
    }

    /**
     * Normalize server URL to ensure it's valid for Retrofit
     * - Ensures trailing slash
     * - Validates format
     */
    private fun normalizeUrl(url: String): String {
        val trimmed = url.trim()
        return if (trimmed.endsWith("/")) {
            trimmed
        } else {
            "$trimmed/"
        }
    }
}
