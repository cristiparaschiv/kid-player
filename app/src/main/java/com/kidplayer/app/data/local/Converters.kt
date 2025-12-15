package com.kidplayer.app.data.local

import androidx.room.TypeConverter
import com.kidplayer.app.data.local.entity.DownloadStatus

/**
 * Room type converters for custom types
 */
class Converters {

    /**
     * Convert DownloadStatus enum to String for storage
     */
    @TypeConverter
    fun fromDownloadStatus(status: DownloadStatus): String {
        return status.name
    }

    /**
     * Convert String to DownloadStatus enum
     */
    @TypeConverter
    fun toDownloadStatus(value: String): DownloadStatus {
        return try {
            DownloadStatus.valueOf(value)
        } catch (e: IllegalArgumentException) {
            DownloadStatus.PENDING
        }
    }
}
