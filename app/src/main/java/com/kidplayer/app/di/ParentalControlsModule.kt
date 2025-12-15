package com.kidplayer.app.di

import com.kidplayer.app.data.local.PinManager
import com.kidplayer.app.data.local.ScreenTimeManager
import com.kidplayer.app.data.local.SecurePreferences
import com.kidplayer.app.data.repository.ParentalControlsRepositoryImpl
import com.kidplayer.app.domain.repository.ParentalControlsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for parental controls dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object ParentalControlsModule {

    @Provides
    @Singleton
    fun providePinManager(): PinManager {
        return PinManager()
    }

    @Provides
    @Singleton
    fun provideScreenTimeManager(
        securePreferences: SecurePreferences
    ): ScreenTimeManager {
        return ScreenTimeManager(securePreferences)
    }

    @Provides
    @Singleton
    fun provideParentalControlsRepository(
        securePreferences: SecurePreferences,
        pinManager: PinManager,
        screenTimeManager: ScreenTimeManager
    ): ParentalControlsRepository {
        return ParentalControlsRepositoryImpl(
            securePreferences = securePreferences,
            pinManager = pinManager,
            screenTimeManager = screenTimeManager
        )
    }
}
