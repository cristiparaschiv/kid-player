package com.kidplayer.app.presentation.games

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.kidplayer.app.R
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Game intro sound resource IDs mapped by game ID
 */
object GameIntroSounds {
    private val introMap = mapOf(
        "memory" to R.raw.intro_match,
        "tictactoe" to R.raw.intro_tic_tac_toe,
        "puzzle" to R.raw.intro_puzzle,
        "match3" to R.raw.intro_match_3,
        "coloring" to R.raw.intro_coloring,
        "sliding" to R.raw.intro_sliding_puzzle,
        "gridpuzzle" to R.raw.intro_grid_puzzle,
        "pattern" to R.raw.intro_pattern,
        "colormix" to R.raw.intro_color_mix,
        "lettermatch" to R.raw.intro_letter_match,
        "maze" to R.raw.intro_maze,
        "dots" to R.raw.intro_connect_dots,
        "addition" to R.raw.intro_addition,
        "subtraction" to R.raw.intro_subtraction,
        "numberbonds" to R.raw.intro_number_bonds,
        "compare" to R.raw.intro_compare,
        "oddoneout" to R.raw.intro_odd_one_out,
        "sudoku" to R.raw.intro_sudoku,
        "ballsort" to R.raw.intro_ball_sort,
        "hangman" to R.raw.intro_hangman,
        "crossword" to R.raw.intro_crossword,
        "counting" to R.raw.intro_counting,
        "shapes" to R.raw.intro_shapes,
        "spelling" to R.raw.intro_spelling,
        "wordsearch" to R.raw.intro_word_search,
        "spotdiff" to R.raw.intro_spot_the_diference
    )

    fun getIntroSound(gameId: String): Int? = introMap[gameId]
}

/**
 * Manages background music and sound effects for all games.
 * Music plays continuously while in the games section and loops automatically.
 * Sound effects play once and can play alongside music.
 */
@Singleton
class GameMusicManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var mediaPlayer: MediaPlayer? = null
    private var soundEffectPlayer: MediaPlayer? = null
    private var isPaused: Boolean = false
    private var currentPosition: Int = 0
    private val handler = Handler(Looper.getMainLooper())

    companion object {
        private const val MUSIC_VOLUME_NORMAL = 0.5f
        private const val MUSIC_VOLUME_DUCKED = 0.15f  // Lower when voice plays
    }

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
                    setVolume(MUSIC_VOLUME_NORMAL, MUSIC_VOLUME_NORMAL)
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

    /**
     * Play a one-shot sound effect (doesn't interrupt background music)
     * Ducks the music volume while voice plays for better clarity
     */
    private fun playSoundEffect(resourceId: Int) {
        // Post to handler to ensure we're on the main thread
        handler.post {
            try {
                // Release previous sound effect player if exists
                soundEffectPlayer?.let { player ->
                    try {
                        player.release()
                    } catch (e: Exception) {
                        Timber.w(e, "Error releasing previous sound effect player")
                    }
                }
                soundEffectPlayer = null

                val player = MediaPlayer.create(context, resourceId)
                if (player == null) {
                    Timber.e("MediaPlayer.create returned null for resource: $resourceId")
                    return@post
                }

                // Duck the music volume while voice plays
                mediaPlayer?.setVolume(MUSIC_VOLUME_DUCKED, MUSIC_VOLUME_DUCKED)

                soundEffectPlayer = player.apply {
                    setVolume(1.0f, 1.0f)  // Full volume for sound effects
                    setOnCompletionListener { mp ->
                        // Restore music volume
                        mediaPlayer?.setVolume(MUSIC_VOLUME_NORMAL, MUSIC_VOLUME_NORMAL)
                        try {
                            mp.release()
                        } catch (e: Exception) {
                            Timber.w(e, "Error releasing sound effect player on completion")
                        }
                        if (soundEffectPlayer == mp) {
                            soundEffectPlayer = null
                        }
                    }
                    setOnErrorListener { mp, what, extra ->
                        Timber.e("MediaPlayer error: what=$what, extra=$extra")
                        // Restore music volume on error too
                        mediaPlayer?.setVolume(MUSIC_VOLUME_NORMAL, MUSIC_VOLUME_NORMAL)
                        try {
                            mp.release()
                        } catch (e: Exception) {
                            Timber.w(e, "Error releasing sound effect player on error")
                        }
                        if (soundEffectPlayer == mp) {
                            soundEffectPlayer = null
                        }
                        true
                    }
                    start()
                }
                Timber.d("Playing sound effect: $resourceId")
            } catch (e: Exception) {
                Timber.e(e, "Failed to play sound effect: $resourceId")
                // Restore music volume on exception
                mediaPlayer?.setVolume(MUSIC_VOLUME_NORMAL, MUSIC_VOLUME_NORMAL)
            }
        }
    }

    /**
     * Play the intro sound for a specific game.
     * The background music continues playing.
     */
    fun playGameIntro(gameId: String) {
        GameIntroSounds.getIntroSound(gameId)?.let { soundRes ->
            playSoundEffect(soundRes)
            Timber.d("Playing intro for game: $gameId")
        } ?: Timber.w("No intro sound found for game: $gameId")
    }

    /**
     * Play the "pick a game" sound when entering Games screen
     */
    fun playPickAGame() {
        playSoundEffect(R.raw.pick_a_game)
    }

    /**
     * Play the completion sound when a game is finished
     */
    fun playGameComplete() {
        playSoundEffect(R.raw.you_did_well)
    }

    /**
     * Stop any currently playing sound effect
     */
    fun stopSoundEffect() {
        try {
            soundEffectPlayer?.let { player ->
                if (player.isPlaying) {
                    player.stop()
                }
                player.release()
            }
            soundEffectPlayer = null
        } catch (e: Exception) {
            Timber.e(e, "Failed to stop sound effect")
        }
    }

    /**
     * Release all audio resources (call when leaving games section)
     */
    fun releaseAll() {
        stopMusic()
        stopSoundEffect()
    }
}
