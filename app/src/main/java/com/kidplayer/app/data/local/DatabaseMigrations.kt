package com.kidplayer.app.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Database migrations for AppDatabase
 * Each migration handles schema changes between versions
 */
object DatabaseMigrations {

    /**
     * Migration from version 4 to 5
     * Adds userId column to media_items and libraries tables
     *
     * This migration is DESTRUCTIVE - it clears existing data because:
     * 1. We cannot infer which user the cached data belongs to
     * 2. The cached data may be from a previous user session
     * 3. User permissions must be strictly enforced to prevent security issues
     *
     * Alternative: We could add a nullable userId column and filter out null entries,
     * but that would still show cached data to the wrong user temporarily.
     */
    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Since we cannot determine which user the existing cached data belongs to,
            // we need to clear all cached media items and libraries
            // This ensures users only see data they have permission to access

            // Drop and recreate media_items table with userId column
            database.execSQL("DROP TABLE IF EXISTS media_items")
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS media_items (
                    id TEXT PRIMARY KEY NOT NULL,
                    title TEXT NOT NULL,
                    overview TEXT,
                    thumbnailUrl TEXT,
                    backdropUrl TEXT,
                    duration INTEGER NOT NULL,
                    jellyfinItemId TEXT NOT NULL,
                    type TEXT NOT NULL,
                    seriesName TEXT,
                    seasonNumber INTEGER,
                    episodeNumber INTEGER,
                    year INTEGER,
                    isDownloaded INTEGER NOT NULL,
                    downloadProgress REAL NOT NULL,
                    watchedPercentage REAL NOT NULL,
                    localFilePath TEXT,
                    libraryId TEXT,
                    userId TEXT NOT NULL,
                    addedTimestamp INTEGER NOT NULL,
                    lastModifiedTimestamp INTEGER NOT NULL
                )
            """.trimIndent())

            // Drop and recreate libraries table with userId column
            database.execSQL("DROP TABLE IF EXISTS libraries")
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS libraries (
                    id TEXT PRIMARY KEY NOT NULL,
                    name TEXT NOT NULL,
                    collectionType TEXT,
                    isEnabled INTEGER NOT NULL,
                    userId TEXT NOT NULL,
                    addedTimestamp INTEGER NOT NULL,
                    lastModifiedTimestamp INTEGER NOT NULL
                )
            """.trimIndent())

            // Drop and recreate downloads table with userId column
            database.execSQL("DROP TABLE IF EXISTS downloads")
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS downloads (
                    id TEXT PRIMARY KEY NOT NULL,
                    mediaItemId TEXT NOT NULL,
                    userId TEXT NOT NULL,
                    status TEXT NOT NULL,
                    progress REAL NOT NULL,
                    downloadedBytes INTEGER NOT NULL,
                    totalBytes INTEGER NOT NULL,
                    localFilePath TEXT,
                    errorMessage TEXT,
                    workRequestId TEXT,
                    startedTimestamp INTEGER NOT NULL,
                    completedTimestamp INTEGER,
                    lastModifiedTimestamp INTEGER NOT NULL
                )
            """.trimIndent())

            // Note: Other tables (watch_history, favorites, search_history)
            // may also need userId in the future for proper multi-user support
        }
    }

    /**
     * Migration from version 5 to 6
     * Adds playbackPositionTicks column to media_items table
     * for resume position sync with Jellyfin
     */
    val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add playbackPositionTicks column with default value of 0
            database.execSQL(
                "ALTER TABLE media_items ADD COLUMN playbackPositionTicks INTEGER NOT NULL DEFAULT 0"
            )
        }
    }

    /**
     * Migration from version 6 to 7
     * Adds player_rewards table for star reward system
     */
    val MIGRATION_6_7 = object : Migration(6, 7) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS player_rewards (
                    id INTEGER PRIMARY KEY NOT NULL,
                    totalStars INTEGER NOT NULL DEFAULT 0,
                    lastUpdated INTEGER NOT NULL
                )
            """.trimIndent())
        }
    }
}
