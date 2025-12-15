package com.kidplayer.app.presentation.player.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

/**
 * Composable that displays ExoPlayer video player
 * Uses AndroidView to integrate Media3 PlayerView with Compose
 */
@UnstableApi
@Composable
fun VideoPlayer(
    player: ExoPlayer,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val playerView = remember {
        PlayerView(context).apply {
            this.player = player
            useController = false // We'll use custom controls
            setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
            setBackgroundColor(android.graphics.Color.BLACK)
        }
    }

    DisposableEffect(player) {
        playerView.player = player

        onDispose {
            playerView.player = null
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            factory = { playerView },
            modifier = Modifier.fillMaxSize()
        )
    }
}
