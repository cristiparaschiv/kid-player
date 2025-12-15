package com.kidplayer.app.di

import android.content.Context
import androidx.room.Room
import com.kidplayer.app.data.local.AppDatabase
import com.kidplayer.app.data.local.DatabaseMigrations
import com.kidplayer.app.data.local.dao.DownloadDao
import com.kidplayer.app.data.local.dao.FavoriteDao
import com.kidplayer.app.data.local.dao.LibraryDao
import com.kidplayer.app.data.local.dao.MediaItemDao
import com.kidplayer.app.data.local.dao.SearchHistoryDao
import com.kidplayer.app.data.local.dao.WatchHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dependency injection module for Room database
 * Provides database instance and DAOs
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .addMigrations(DatabaseMigrations.MIGRATION_4_5)
            .fallbackToDestructiveMigration() // Fallback for other versions
            .build()
    }

    @Provides
    @Singleton
    fun provideMediaItemDao(database: AppDatabase): MediaItemDao {
        return database.mediaItemDao()
    }

    @Provides
    @Singleton
    fun provideLibraryDao(database: AppDatabase): LibraryDao {
        return database.libraryDao()
    }

    @Provides
    @Singleton
    fun provideDownloadDao(database: AppDatabase): DownloadDao {
        return database.downloadDao()
    }

    @Provides
    @Singleton
    fun provideWatchHistoryDao(database: AppDatabase): WatchHistoryDao {
        return database.watchHistoryDao()
    }

    @Provides
    @Singleton
    fun provideFavoriteDao(database: AppDatabase): FavoriteDao {
        return database.favoriteDao()
    }

    @Provides
    @Singleton
    fun provideSearchHistoryDao(database: AppDatabase): SearchHistoryDao {
        return database.searchHistoryDao()
    }
}
