package com.kidplayer.app.di

import com.kidplayer.app.data.repository.FavoritesRepositoryImpl
import com.kidplayer.app.data.repository.SearchRepositoryImpl
import com.kidplayer.app.data.repository.WatchHistoryRepositoryImpl
import com.kidplayer.app.domain.repository.FavoritesRepository
import com.kidplayer.app.domain.repository.SearchRepository
import com.kidplayer.app.domain.repository.WatchHistoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for binding repository implementations
 * Phase 6: New repositories for watch history, favorites, and search
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWatchHistoryRepository(
        impl: WatchHistoryRepositoryImpl
    ): WatchHistoryRepository

    @Binds
    @Singleton
    abstract fun bindFavoritesRepository(
        impl: FavoritesRepositoryImpl
    ): FavoritesRepository

    @Binds
    @Singleton
    abstract fun bindSearchRepository(
        impl: SearchRepositoryImpl
    ): SearchRepository
}
