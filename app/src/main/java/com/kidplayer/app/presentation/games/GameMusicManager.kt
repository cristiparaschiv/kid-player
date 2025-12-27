package com.kidplayer.app.presentation.games

import android.content.Context
import android.media.MediaPlayer
import com.kidplayer.app.R
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages background music for all games.
 * Music plays continuously while in the games section and loops automatically.
 */
@Singleton
class GameMusicManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var mediaPlayer: MediaPlayer? = null
    private var isPaused: Boolean = false
    private var currentPosition: Int = 0

    /**
     * Start playing the background music.
     * If already playing, does nothing.
     */
    fun startMusic() {
        if (mediaPlayer?.isPlaying == true) {
            Timber.d("Music already playing")
            return
        }

        try {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(context, R.raw.candy_town).apply {
                    isLooping = true
                    setVolume(0.5f, 0.5f)  // 50% volume for background music
                }
            }

            if (isPaused && currentPosition > 0) {
                mediaPlayer?.seekTo(currentPosition)
            }

            mediaPlayer?.start()
            isPaused = false
            Timber.d("Game music started")
        } catch (e: Exception) {
            Timber.e(e, "Failed to start game music")
        }
    }

    /**
     * Pause the music, saving the current position.
     */
    fun pauseMusic() {
        try {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    currentPosition = player.currentPosition
                    player.pause()
                    isPaused = true
                    Timber.d("Game music paused at position: $currentPosition")
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to pause game music")
        }
    }

    /**
     * Resume playing from where it was paused.
     */
    fun resumeMusic() {
        if (isPaused) {
            startMusic()
        }
    }

    /**
     * Stop the music and release resources.
     */
    fun stopMusic() {
        try {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    player.stop()
                }
                player.release()
            }
            mediaPlayer = null
            isPaused = false
            currentPosition = 0
            Timber.d("Game music stopped and released")
        } catch (e: Exception) {
            Timber.e(e, "Failed to stop game music")
        }
    }

    /**
     * Set the volume (0.0 to 1.0)
     */
    fun setVolume(volume: Float) {
        val clampedVolume = volume.coerceIn(0f, 1f)
        mediaPlayer?.setVolume(clampedVolume, clampedVolume)
    }

    /**
     * Check if music is currently playing
     */
    fun isPlaying(): Boolean = mediaPlayer?.isPlaying == true
}
