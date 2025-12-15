package com.kidplayer.app.domain.model

/**
 * Domain model for autoplay configuration
 * Configurable by parents in parental controls
 */
data class AutoplayConfig(
    val enabled: Boolean = true,
    val countdownSeconds: Int = 5,
    val onlyInSeries: Boolean = false
)
