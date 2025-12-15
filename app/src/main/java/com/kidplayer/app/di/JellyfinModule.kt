package com.kidplayer.app.di

import com.kidplayer.app.data.repository.JellyfinRepositoryImpl
import com.kidplayer.app.domain.repository.JellyfinRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for Jellyfin-related dependencies
 * Provides repository implementations and bindings
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class JellyfinModule {

    /**
     * Binds the JellyfinRepository interface to its implementation
     */
    @Binds
    @Singleton
    abstract fun bindJellyfinRepository(
        implementation: JellyfinRepositoryImpl
    ): JellyfinRepository
}
