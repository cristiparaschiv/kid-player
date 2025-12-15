package com.kidplayer.app.data.util

import android.content.Context
import android.os.StatFs
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages storage space for downloaded videos
 * Handles space checking, cleanup, and file management
 */
@Singleton
class StorageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * Get available storage space in bytes
     */
    fun getAvailableSpace(): Long {
        return try {
            val downloadsDir = getDownloadsDirectory()
            val stat = StatFs(downloadsDir.path)
            stat.availableBytes
        } catch (e: Exception) {
            Timber.e(e, "Error getting available space")
            0L
        }
    }

    /**
     * Get total storage space in bytes
     */
    fun getTotalSpace(): Long {
        return try {
            val downloadsDir = getDownloadsDirectory()
            val stat = StatFs(downloadsDir.path)
            stat.totalBytes
        } catch (e: Exception) {
            Timber.e(e, "Error getting total space")
            0L
        }
    }

    /**
     * Get used storage space by downloads in bytes
     */
    fun getUsedSpaceByDownloads(): Long {
        return try {
            val downloadsDir = getDownloadsDirectory()
            calculateDirectorySize(downloadsDir)
        } catch (e: Exception) {
            Timber.e(e, "Error calculating used space")
            0L
        }
    }

    /**
     * Check if there's enough space for a download
     * @param requiredBytes Size of file to download
     * @return true if enough space available
     */
    fun hasEnoughSpace(requiredBytes: Long): Boolean {
        val availableSpace = getAvailableSpace()
        val requiredWithBuffer = requiredBytes + STORAGE_BUFFER_BYTES

        Timber.d("Storage check: required=$requiredBytes, available=$availableSpace")
        return availableSpace >= requiredWithBuffer
    }

    /**
     * Check if storage is low (less than minimum required)
     */
    fun isStorageLow(): Boolean {
        val availableSpace = getAvailableSpace()
        return availableSpace < MIN_STORAGE_BYTES
    }

    /**
     * Get downloads directory, creating it if it doesn't exist
     */
    fun getDownloadsDirectory(): File {
        val downloadsDir = File(context.getExternalFilesDir(null), "downloads")
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }
        return downloadsDir
    }

    /**
     * Delete a downloaded video file
     * @param filePath Path to the file
     * @return true if deleted successfully
     */
    fun deleteDownloadedFile(filePath: String): Boolean {
        return try {
            val file = File(filePath)
            if (file.exists()) {
                val deleted = file.delete()
                if (deleted) {
                    Timber.d("Deleted file: $filePath")
                } else {
                    Timber.w("Failed to delete file: $filePath")
                }
                deleted
            } else {
                Timber.w("File not found: $filePath")
                false
            }
        } catch (e: Exception) {
            Timber.e(e, "Error deleting file: $filePath")
            false
        }
    }

    /**
     * Delete all downloaded files
     * @return Number of files deleted
     */
    fun deleteAllDownloads(): Int {
        return try {
            val downloadsDir = getDownloadsDirectory()
            var deletedCount = 0

            downloadsDir.listFiles()?.forEach { file ->
                if (file.isFile && file.delete()) {
                    deletedCount++
                }
            }

            Timber.d("Deleted $deletedCount files")
            deletedCount
        } catch (e: Exception) {
            Timber.e(e, "Error deleting all downloads")
            0
        }
    }

    /**
     * Get file size
     * @param filePath Path to the file
     * @return Size in bytes, or 0 if file doesn't exist
     */
    fun getFileSize(filePath: String): Long {
        return try {
            val file = File(filePath)
            if (file.exists() && file.isFile) {
                file.length()
            } else {
                0L
            }
        } catch (e: Exception) {
            Timber.e(e, "Error getting file size: $filePath")
            0L
        }
    }

    /**
     * Check if file exists
     */
    fun fileExists(filePath: String): Boolean {
        return try {
            File(filePath).exists()
        } catch (e: Exception) {
            Timber.e(e, "Error checking file existence: $filePath")
            false
        }
    }

    /**
     * Calculate total size of a directory
     */
    private fun calculateDirectorySize(directory: File): Long {
        var size = 0L
        directory.listFiles()?.forEach { file ->
            size += if (file.isDirectory) {
                calculateDirectorySize(file)
            } else {
                file.length()
            }
        }
        return size
    }

    /**
     * Get storage info as formatted string
     */
    fun getStorageInfo(): StorageInfo {
        val available = getAvailableSpace()
        val total = getTotalSpace()
        val used = getUsedSpaceByDownloads()

        return StorageInfo(
            availableBytes = available,
            totalBytes = total,
            usedByDownloads = used,
            availableFormatted = formatBytes(available),
            totalFormatted = formatBytes(total),
            usedByDownloadsFormatted = formatBytes(used)
        )
    }

    /**
     * Format bytes to human-readable string
     */
    private fun formatBytes(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "%.2f KB".format(bytes / 1024.0)
            bytes < 1024 * 1024 * 1024 -> "%.2f MB".format(bytes / (1024.0 * 1024.0))
            else -> "%.2f GB".format(bytes / (1024.0 * 1024.0 * 1024.0))
        }
    }

    companion object {
        // Minimum storage space required (2GB)
        const val MIN_STORAGE_BYTES = 2L * 1024 * 1024 * 1024

        // Buffer to keep free (500MB)
        const val STORAGE_BUFFER_BYTES = 500L * 1024 * 1024
    }
}

/**
 * Storage information data class
 */
data class StorageInfo(
    val availableBytes: Long,
    val totalBytes: Long,
    val usedByDownloads: Long,
    val availableFormatted: String,
    val totalFormatted: String,
    val usedByDownloadsFormatted: String
)
