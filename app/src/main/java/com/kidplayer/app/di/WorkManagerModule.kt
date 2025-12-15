package com.kidplayer.app.di

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.kidplayer.app.data.worker.PeriodicSyncWorker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Dependency injection module for WorkManager
 * Provides WorkManager instance and schedules periodic workers
 */
@Module
@InstallIn(SingletonComponent::class)
object WorkManagerModule {

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    /**
     * Schedule periodic sync worker
     * This is called when the WorkManager is first injected
     */
    @Provides
    @Singleton
    fun providePeriodicSyncScheduler(
        workManager: WorkManager
    ): PeriodicSyncScheduler {
        return PeriodicSyncScheduler(workManager)
    }
}

/**
 * Helper class to schedule periodic sync worker
 */
class PeriodicSyncScheduler(
    private val workManager: WorkManager
) {
    init {
        schedulePeriodicSync()
    }

    /**
     * Schedule daily periodic sync worker
     * Runs once every 24 hours on WiFi with battery constraints
     */
    private fun schedulePeriodicSync() {
        try {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED) // WiFi only
                .setRequiresBatteryNotLow(true)
                .setRequiresStorageNotLow(true)
                .build()

            val periodicSyncRequest = PeriodicWorkRequestBuilder<PeriodicSyncWorker>(
                repeatInterval = 24,
                repeatIntervalTimeUnit = TimeUnit.HOURS,
                flexTimeInterval = 2, // 2 hour flex time
                flexTimeIntervalUnit = TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .addTag(TAG_PERIODIC_SYNC)
                .build()

            workManager.enqueueUniquePeriodicWork(
                WORK_NAME_PERIODIC_SYNC,
                ExistingPeriodicWorkPolicy.KEEP, // Keep existing work if already scheduled
                periodicSyncRequest
            )

            Timber.d("Periodic sync worker scheduled")
        } catch (e: Exception) {
            Timber.e(e, "Error scheduling periodic sync worker")
        }
    }

    companion object {
        private const val WORK_NAME_PERIODIC_SYNC = "periodic_sync"
        private const val TAG_PERIODIC_SYNC = "periodic_sync"
    }
}
