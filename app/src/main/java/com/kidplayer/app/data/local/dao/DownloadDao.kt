package com.kidplayer.app.data.local.dao

import androidx.room.*
import com.kidplayer.app.data.local.entity.DownloadEntity
import com.kidplayer.app.data.local.entity.DownloadStatus
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Download entities
 * Manages download tracking and status updates
 */
@Dao
interface DownloadDao {

    /**
     * Get all downloads for a specific user
     */
    @Query("SELECT * FROM downloads WHERE userId = :userId ORDER BY startedTimestamp DESC")
    fun getAllDownloads(userId: String): Flow<List<DownloadEntity>>

    /**
     * Get downloads by status for a specific user
     */
    @Query("SELECT * FROM downloads WHERE userId = :userId AND status = :status ORDER BY startedTimestamp DESC")
    fun getDownloadsByStatus(userId: String, status: DownloadStatus): Flow<List<DownloadEntity>>

    /**
     * Get download for a specific media item and user
     */
    @Query("SELECT * FROM downloads WHERE userId = :userId AND mediaItemId = :mediaItemId LIMIT 1")
    suspend fun getDownloadByMediaItemId(userId: String, mediaItemId: String): DownloadEntity?

    /**
     * Get download for a specific media item and user (Flow)
     */
    @Query("SELECT * FROM downloads WHERE userId = :userId AND mediaItemId = :mediaItemId LIMIT 1")
    fun observeDownloadByMediaItemId(userId: String, mediaItemId: String): Flow<DownloadEntity?>

    /**
     * Get download by ID
     */
    @Query("SELECT * FROM downloads WHERE id = :downloadId LIMIT 1")
    suspend fun getDownloadById(downloadId: String): DownloadEntity?

    /**
     * Get download for a specific media item without user filter (for background workers)
     * Note: mediaItemId is globally unique, safe for internal use
     */
    @Query("SELECT * FROM downloads WHERE mediaItemId = :mediaItemId LIMIT 1")
    suspend fun getDownloadByMediaItemIdUnscoped(mediaItemId: String): DownloadEntity?

    /**
     * Get downloads by status without user filter (for background workers)
     */
    @Query("SELECT * FROM downloads WHERE status = :status ORDER BY startedTimestamp DESC")
    fun getDownloadsByStatusUnscoped(status: DownloadStatus): Flow<List<DownloadEntity>>

    /**
     * Get download by work request ID
     */
    @Query("SELECT * FROM downloads WHERE workRequestId = :workRequestId LIMIT 1")
    suspend fun getDownloadByWorkRequestId(workRequestId: String): DownloadEntity?

    /**
     * Get all pending downloads for a specific user
     */
    @Query("SELECT * FROM downloads WHERE userId = :userId AND status = 'PENDING' ORDER BY startedTimestamp ASC")
    suspend fun getPendingDownloads(userId: String): List<DownloadEntity>

    /**
     * Get all active downloads (pending or downloading) for a specific user
     */
    @Query("SELECT * FROM downloads WHERE userId = :userId AND status IN ('PENDING', 'DOWNLOADING') ORDER BY startedTimestamp ASC")
    fun getActiveDownloads(userId: String): Flow<List<DownloadEntity>>

    /**
     * Get all completed downloads for a specific user
     */
    @Query("SELECT * FROM downloads WHERE userId = :userId AND status = 'COMPLETED' ORDER BY completedTimestamp DESC")
    fun getCompletedDownloads(userId: String): Flow<List<DownloadEntity>>

    /**
     * Insert a download
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(download: DownloadEntity)

    /**
     * Insert multiple downloads
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownloads(downloads: List<DownloadEntity>)

    /**
     * Update a download
     */
    @Update
    suspend fun updateDownload(download: DownloadEntity)

    /**
     * Update download progress
     */
    @Query("""
        UPDATE downloads
        SET progress = :progress,
            downloadedBytes = :downloadedBytes,
            totalBytes = :totalBytes,
            lastModifiedTimestamp = :timestamp
        WHERE id = :downloadId
    """)
    suspend fun updateDownloadProgress(
        downloadId: String,
        progress: Float,
        downloadedBytes: Long,
        totalBytes: Long,
        timestamp: Long = System.currentTimeMillis()
    )

    /**
     * Update download status
     */
    @Query("""
        UPDATE downloads
        SET status = :status,
            lastModifiedTimestamp = :timestamp
        WHERE id = :downloadId
    """)
    suspend fun updateDownloadStatus(
        downloadId: String,
        status: DownloadStatus,
        timestamp: Long = System.currentTimeMillis()
    )

    /**
     * Update download status with completion
     */
    @Query("""
        UPDATE downloads
        SET status = :status,
            localFilePath = :filePath,
            completedTimestamp = :completedTimestamp,
            lastModifiedTimestamp = :timestamp
        WHERE id = :downloadId
    """)
    suspend fun completeDownload(
        downloadId: String,
        status: DownloadStatus,
        filePath: String?,
        completedTimestamp: Long = System.currentTimeMillis(),
        timestamp: Long = System.currentTimeMillis()
    )

    /**
     * Mark download as failed with error message
     */
    @Query("""
        UPDATE downloads
        SET status = 'FAILED',
            errorMessage = :errorMessage,
            lastModifiedTimestamp = :timestamp
        WHERE id = :downloadId
    """)
    suspend fun markDownloadAsFailed(
        downloadId: String,
        errorMessage: String,
        timestamp: Long = System.currentTimeMillis()
    )

    /**
     * Update work request ID
     */
    @Query("""
        UPDATE downloads
        SET workRequestId = :workRequestId,
            lastModifiedTimestamp = :timestamp
        WHERE id = :downloadId
    """)
    suspend fun updateWorkRequestId(
        downloadId: String,
        workRequestId: String,
        timestamp: Long = System.currentTimeMillis()
    )

    /**
     * Delete a download
     */
    @Query("DELETE FROM downloads WHERE id = :downloadId")
    suspend fun deleteDownload(downloadId: String)

    /**
     * Delete download by media item ID
     */
    @Query("DELETE FROM downloads WHERE mediaItemId = :mediaItemId")
    suspend fun deleteDownloadByMediaItemId(mediaItemId: String)

    /**
     * Delete all downloads with specific status
     */
    @Query("DELETE FROM downloads WHERE status = :status")
    suspend fun deleteDownloadsByStatus(status: DownloadStatus)

    /**
     * Delete all completed downloads
     */
    @Query("DELETE FROM downloads WHERE status = 'COMPLETED'")
    suspend fun deleteCompletedDownloads()

    /**
     * Delete all downloads
     */
    @Query("DELETE FROM downloads")
    suspend fun deleteAllDownloads()

    /**
     * Get count of downloads by status for a specific user
     */
    @Query("SELECT COUNT(*) FROM downloads WHERE userId = :userId AND status = :status")
    suspend fun getDownloadCountByStatus(userId: String, status: DownloadStatus): Int

    /**
     * Get total size of all completed downloads for a specific user
     */
    @Query("SELECT SUM(totalBytes) FROM downloads WHERE userId = :userId AND status = 'COMPLETED'")
    suspend fun getTotalDownloadedSize(userId: String): Long?
}
