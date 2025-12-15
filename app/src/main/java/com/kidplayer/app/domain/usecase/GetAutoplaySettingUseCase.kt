package com.kidplayer.app.domain.usecase

import com.kidplayer.app.data.local.SecurePreferences
import javax.inject.Inject

/**
 * Use case for getting the autoplay setting
 * Returns true if autoplay is enabled (default), false otherwise
 */
class GetAutoplaySettingUseCase @Inject constructor(
    private val securePreferences: SecurePreferences
) {
    suspend operator fun invoke(): Boolean {
        return securePreferences.getAutoplayEnabled()
    }
}
