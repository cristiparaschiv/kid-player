package com.kidplayer.app.di

import android.content.Context
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.HttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.kidplayer.app.data.local.SecurePreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.runBlocking

/**
 * Hilt module for providing ExoPlayer instances
 * Scoped to ViewModel to ensure proper lifecycle management
 */
@Module
@InstallIn(ViewModelComponent::class)
object PlayerModule {

    /**
     * Provides HTTP data source factory with Jellyfin authentication headers
     */
    @UnstableApi
    @Provides
    @ViewModelScoped
    fun provideHttpDataSourceFactory(
        @ApplicationContext context: Context,
        securePreferences: SecurePreferences
    ): HttpDataSource.Factory {
        // Get auth token from secure preferences
        val authToken = runBlocking {
            try {
                securePreferences.getAuthToken() ?: ""
            } catch (e: Exception) {
                ""
            }
        }

        return DefaultHttpDataSource.Factory()
            .setUserAgent("KidPlayer/1.0")
            .setDefaultRequestProperties(
                if (authToken.isNotEmpty()) {
                    mapOf("X-MediaBrowser-Token" to authToken)
                } else {
                    emptyMap()
                }
            )
            .setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS)
            .setReadTimeoutMs(DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS)
            .setAllowCrossProtocolRedirects(true)
    }

    /**
     * Provides data source factory that handles both HTTP and local files
     */
    @UnstableApi
    @Provides
    @ViewModelScoped
    fun provideDataSourceFactory(
        @ApplicationContext context: Context,
        httpDataSourceFactory: HttpDataSource.Factory
    ): DefaultDataSource.Factory {
        return DefaultDataSource.Factory(context, httpDataSourceFactory)
    }

    /**
     * Provides ExoPlayer instance configured for streaming
     * Scoped to ViewModel to ensure it's released when ViewModel is cleared
     *
     * Audio Configuration:
     * - Enabled audio offload for better battery efficiency
     * - Audio focus handling for interruptions
     * - Automatic audio track selection
     * - Support for multiple audio codecs
     */
    @UnstableApi
    @Provides
    @ViewModelScoped
    fun provideExoPlayer(
        @ApplicationContext context: Context,
        dataSourceFactory: DefaultDataSource.Factory
    ): ExoPlayer {
        val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)
            .setLiveTargetOffsetMs(5000) // 5 second live target offset

        return ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .setHandleAudioBecomingNoisy(true) // Pause when headphones disconnected
            .setWakeMode(androidx.media3.common.C.WAKE_MODE_LOCAL) // Keep screen on during playback
            .setSeekBackIncrementMs(10_000) // 10 seconds back
            .setSeekForwardIncrementMs(10_000) // 10 seconds forward
            // Audio configuration for better compatibility
            .setAudioAttributes(
                androidx.media3.common.AudioAttributes.Builder()
                    .setContentType(androidx.media3.common.C.AUDIO_CONTENT_TYPE_MOVIE)
                    .setUsage(androidx.media3.common.C.USAGE_MEDIA)
                    .build(),
                true // Handle audio focus
            )
            .build()
            .apply {
                // Configure player behavior
                playWhenReady = false // Don't auto-play initially
                repeatMode = Player.REPEAT_MODE_OFF // No repeat by default

                // Enable automatic track selection
                // ExoPlayer will automatically select the best audio track
                trackSelectionParameters = trackSelectionParameters
                    .buildUpon()
                    .setPreferredAudioLanguage("en") // Prefer English, fallback to others
                    .setMaxAudioChannelCount(6) // Support up to 5.1 surround
                    .setMaxAudioBitrate(Int.MAX_VALUE) // No audio bitrate limit
                    .build()
            }
    }
}
