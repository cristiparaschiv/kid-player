package com.kidplayer.app.presentation.components

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.kidplayer.app.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Sound types available in the app
 */
enum class SoundType {
    CLICK,      // Button click
    TAP,        // Tile/cell tap
    SWITCH,     // Toggle switch
    CORRECT,    // Correct answer
    WRONG,      // Wrong answer
    STAR,       // Star earned
    COMPLETE    // Level/game complete
}

/**
 * Manager for playing UI sound effects
 * Uses SoundPool for low-latency playback
 */
@Singleton
class SoundManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var soundPool: SoundPool? = null
    private val soundIds = mutableMapOf<SoundType, Int>()
    private var isInitialized = false
    private var soundEnabled = true

    fun initialize() {
        if (isInitialized) return

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(audioAttributes)
            .build()

        // Load sounds
        soundPool?.let { pool ->
            soundIds[SoundType.CLICK] = pool.load(context, R.raw.sound_click, 1)
            soundIds[SoundType.TAP] = pool.load(context, R.raw.sound_tap, 1)
            soundIds[SoundType.SWITCH] = pool.load(context, R.raw.sound_switch, 1)
            // Reuse sounds for similar effects
            soundIds[SoundType.CORRECT] = pool.load(context, R.raw.sound_tap, 1)
            soundIds[SoundType.WRONG] = pool.load(context, R.raw.sound_click, 1)
            soundIds[SoundType.STAR] = pool.load(context, R.raw.sound_switch, 1)
            soundIds[SoundType.COMPLETE] = pool.load(context, R.raw.sound_switch, 1)
        }

        isInitialized = true
    }

    fun play(soundType: SoundType, volume: Float = 1.0f) {
        if (!soundEnabled || !isInitialized) return

        val soundId = soundIds[soundType] ?: return
        soundPool?.play(
            soundId,
            volume.coerceIn(0f, 1f),
            volume.coerceIn(0f, 1f),
            1,
            0,
            1.0f
        )
    }

    fun playClick() = play(SoundType.CLICK)
    fun playTap() = play(SoundType.TAP)
    fun playCorrect() = play(SoundType.CORRECT, 0.8f)
    fun playWrong() = play(SoundType.WRONG, 0.6f)
    fun playStar() = play(SoundType.STAR)
    fun playComplete() = play(SoundType.COMPLETE)

    fun setSoundEnabled(enabled: Boolean) {
        soundEnabled = enabled
    }

    fun isSoundEnabled(): Boolean = soundEnabled

    fun release() {
        soundPool?.release()
        soundPool = null
        soundIds.clear()
        isInitialized = false
    }
}
