package com.kidplayer.app.presentation.player

import androidx.media3.common.C
import androidx.media3.common.Format
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import timber.log.Timber

/**
 * Utility object for audio track management and diagnostics
 * Provides helper functions to ensure audio is properly configured and playing
 */
@UnstableApi
object AudioTrackUtil {

    /**
     * Ensures that an audio track is selected for playback
     * If no audio track is selected, attempts to select the first available one
     *
     * @param player The ExoPlayer instance
     * @return true if an audio track is available and selected, false otherwise
     */
    fun ensureAudioTrackSelected(player: ExoPlayer): Boolean {
        try {
            val tracks = player.currentTracks

            // Check if any audio track is currently selected
            val hasSelectedAudioTrack = tracks.groups.any { group ->
                group.type == C.TRACK_TYPE_AUDIO && group.isSelected
            }

            if (hasSelectedAudioTrack) {
                Timber.d("Audio track already selected")
                return true
            }

            // No audio track selected - check if any are available
            val audioTrackAvailable = tracks.groups.any { group ->
                group.type == C.TRACK_TYPE_AUDIO && group.length > 0
            }

            if (!audioTrackAvailable) {
                Timber.w("No audio tracks available in this media")
                return false
            }

            // Force audio track selection by updating track selection parameters
            Timber.d("Forcing audio track selection")
            player.trackSelectionParameters = player.trackSelectionParameters
                .buildUpon()
                .setMaxAudioBitrate(Int.MAX_VALUE)
                .setMaxAudioChannelCount(6)
                .clearOverridesOfType(C.TRACK_TYPE_AUDIO)
                .build()

            return true
        } catch (e: Exception) {
            Timber.e(e, "Error ensuring audio track selection")
            return false
        }
    }

    /**
     * Gets a summary of available audio tracks
     *
     * @param player The ExoPlayer instance
     * @return List of audio track information strings
     */
    fun getAudioTracksSummary(player: ExoPlayer): List<String> {
        val summary = mutableListOf<String>()

        try {
            val tracks = player.currentTracks

            tracks.groups.forEach { group ->
                if (group.type == C.TRACK_TYPE_AUDIO) {
                    for (i in 0 until group.length) {
                        val format = group.getTrackFormat(i)
                        val isSelected = group.isTrackSelected(i)
                        summary.add(formatAudioTrackInfo(format, isSelected))
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error getting audio tracks summary")
        }

        return summary
    }

    /**
     * Checks if the player has audio capabilities
     *
     * @param player The ExoPlayer instance
     * @return true if audio playback is supported
     */
    fun hasAudioCapability(player: ExoPlayer): Boolean {
        return try {
            val tracks = player.currentTracks
            tracks.groups.any { it.type == C.TRACK_TYPE_AUDIO }
        } catch (e: Exception) {
            Timber.e(e, "Error checking audio capability")
            false
        }
    }

    /**
     * Gets the currently selected audio track format
     *
     * @param player The ExoPlayer instance
     * @return Format of selected audio track, or null if none selected
     */
    fun getSelectedAudioTrack(player: ExoPlayer): Format? {
        return try {
            val tracks = player.currentTracks

            tracks.groups.forEach { group ->
                if (group.type == C.TRACK_TYPE_AUDIO) {
                    for (i in 0 until group.length) {
                        if (group.isTrackSelected(i)) {
                            return group.getTrackFormat(i)
                        }
                    }
                }
            }
            null
        } catch (e: Exception) {
            Timber.e(e, "Error getting selected audio track")
            null
        }
    }

    /**
     * Formats audio track information for logging/display
     */
    private fun formatAudioTrackInfo(format: Format, isSelected: Boolean): String {
        return buildString {
            append("Codec: ${format.sampleMimeType ?: "unknown"}")
            append(", Channels: ${format.channelCount}")
            append(", Sample Rate: ${format.sampleRate} Hz")
            append(", Bitrate: ${if (format.bitrate > 0) "${format.bitrate / 1000} kbps" else "unknown"}")
            append(", Language: ${format.language ?: "unknown"}")
            append(", Selected: $isSelected")
        }
    }

    /**
     * Creates optimized track selection parameters for audio playback
     * Ensures maximum compatibility and quality
     */
    fun createOptimalAudioTrackParameters(): TrackSelectionParameters {
        return TrackSelectionParameters.Builder()
            .setMaxAudioChannelCount(6) // Support up to 5.1 surround
            .setMaxAudioBitrate(Int.MAX_VALUE) // No bitrate limit
            .setPreferredAudioLanguage("en") // Prefer English
            .clearOverridesOfType(C.TRACK_TYPE_AUDIO) // No manual overrides
            .setForceHighestSupportedBitrate(false) // Balance quality/bandwidth
            .build()
    }

    /**
     * Diagnoses audio playback issues
     *
     * @param player The ExoPlayer instance
     * @return Diagnostic message describing the audio status
     */
    fun diagnoseAudioIssues(player: ExoPlayer): String {
        return try {
            val tracks = player.currentTracks

            when {
                tracks.isEmpty -> "No tracks loaded in player"

                !tracks.groups.any { it.type == C.TRACK_TYPE_AUDIO } ->
                    "No audio tracks available in this media. " +
                            "This could mean: 1) The source file has no audio, " +
                            "2) The streaming URL doesn't include audio streams, " +
                            "3) Audio codec is unsupported"

                !tracks.groups.any { it.type == C.TRACK_TYPE_AUDIO && it.isSelected } ->
                    "Audio tracks available but none selected. " +
                            "Player may need track selection parameter adjustment"

                else -> {
                    val selectedFormat = getSelectedAudioTrack(player)
                    if (selectedFormat != null) {
                        "Audio track selected: ${formatAudioTrackInfo(selectedFormat, true)}"
                    } else {
                        "Audio status unknown"
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error diagnosing audio issues")
            "Error checking audio status: ${e.message}"
        }
    }
}
