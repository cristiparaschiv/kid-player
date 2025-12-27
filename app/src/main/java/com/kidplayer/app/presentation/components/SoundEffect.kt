package com.kidplayer.app.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SoundManagerEntryPoint {
    fun soundManager(): SoundManager
}

/**
 * Remember and provide access to the SoundManager
 * Initializes the sound pool on first access
 */
@Composable
fun rememberSoundManager(): SoundManager {
    val context = LocalContext.current
    val soundManager = remember {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            SoundManagerEntryPoint::class.java
        )
        entryPoint.soundManager().also { it.initialize() }
    }

    return soundManager
}

/**
 * Convenience composable for playing sounds on events
 */
@Composable
fun SoundEffect(
    soundType: SoundType,
    trigger: Any?,
    volume: Float = 1.0f
) {
    val soundManager = rememberSoundManager()

    DisposableEffect(trigger) {
        if (trigger != null) {
            soundManager.play(soundType, volume)
        }
        onDispose { }
    }
}
