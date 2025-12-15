package com.kidplayer.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for tracking download status and progress
 * Separate from MediaItemEntity to allow flexible download management
 */
@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey
    val id: String, // Download ID (UUID)

    val mediaItemId: String, // Reference to MediaItemEntity

    val userId: String, // User ID for user-scoped downloads

    val status: DownloadStatus,

    val progress: Float = 0f, // Progress percentage (0.0 to 1.0)

    val downloadedBytes: Long = 0L,

    val totalBytes: Long = 0L,

    val localFilePath: String? = null,

    val errorMessage: String? = null,

    val workRequestId: String? = null, // WorkManager request ID for tracking

    val startedTimestamp: Long = System.currentTimeMillis(),

    val completedTimestamp: Long? = null,

    val lastModifiedTimestamp: Long = System.currentTimeMillis()
)

/**
 * Download status states
 */
enum class DownloadStatus {
    PENDING,        // Queued but not started
    DOWNLOADING,    // Currently downloading
    COMPLETED,      // Successfully downloaded
    FAILED,         // Download failed
    CANCELLED       // Download was cancelled by user
}
