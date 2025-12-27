package com.kidplayer.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kidplayer.app.data.local.dao.DownloadDao
import com.kidplayer.app.data.local.dao.FavoriteDao
import com.kidplayer.app.data.local.dao.LibraryDao
import com.kidplayer.app.data.local.dao.MediaItemDao
import com.kidplayer.app.data.local.dao.PlayerRewardDao
import com.kidplayer.app.data.local.dao.SearchHistoryDao
import com.kidplayer.app.data.local.dao.WatchHistoryDao
import com.kidplayer.app.data.local.entity.DownloadEntity
import com.kidplayer.app.data.local.entity.FavoriteEntity
import com.kidplayer.app.data.local.entity.LibraryEntity
import com.kidplayer.app.data.local.entity.MediaItemEntity
import com.kidplayer.app.data.local.entity.PlayerRewardEntity
import com.kidplayer.app.data.local.entity.SearchHistoryEntity
import com.kidplayer.app.data.local.entity.WatchHistoryEntity

/**
 * Room database for Kid Player app
 * Provides local caching for media items, libraries, downloads,
 * watch history, favorites, and search history
 */
@Database(
    entities = [
        MediaItemEntity::class,
        LibraryEntity::class,
        DownloadEntity::class,
        WatchHistoryEntity::class,
        FavoriteEntity::class,
        SearchHistoryEntity::class,
        PlayerRewardEntity::class
    ],
    version = 7,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    /**
     * DAO for media item operations
     */
    abstract fun mediaItemDao(): MediaItemDao

    /**
     * DAO for library operations
     */
    abstract fun libraryDao(): LibraryDao

    /**
     * DAO for download operations
     */
    abstract fun downloadDao(): DownloadDao

    /**
     * DAO for watch history operations
     */
    abstract fun watchHistoryDao(): WatchHistoryDao

    /**
     * DAO for favorites operations
     */
    abstract fun favoriteDao(): FavoriteDao

    /**
     * DAO for search history operations
     */
    abstract fun searchHistoryDao(): SearchHistoryDao

    /**
     * DAO for player reward operations
     */
    abstract fun playerRewardDao(): PlayerRewardDao

    companion object {
        const val DATABASE_NAME = "kid_player_database"
    }
}
