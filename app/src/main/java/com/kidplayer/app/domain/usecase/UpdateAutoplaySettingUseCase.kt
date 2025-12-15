package com.kidplayer.app.domain.usecase

import com.kidplayer.app.data.local.SecurePreferences
import timber.log.Timber
import javax.inject.Inject

/**
 * Use case for updating the autoplay setting
 */
class UpdateAutoplaySettingUseCase @Inject constructor(
    private val securePreferences: SecurePreferences
) {
    suspend operator fun invoke(enabled: Boolean) {
        Timber.d("Updating autoplay setting: enabled=$enabled")
        securePreferences.saveAutoplayEnabled(enabled)
    }
}
