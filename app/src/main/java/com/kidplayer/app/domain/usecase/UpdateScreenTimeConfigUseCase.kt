package com.kidplayer.app.domain.usecase

import com.kidplayer.app.domain.model.ScreenTimeConfig
import com.kidplayer.app.domain.repository.ParentalControlsRepository
import javax.inject.Inject

/**
 * Use case for updating screen time configuration
 */
class UpdateScreenTimeConfigUseCase @Inject constructor(
    private val repository: ParentalControlsRepository
) {
    suspend operator fun invoke(config: ScreenTimeConfig): Result<Unit> {
        return repository.updateScreenTimeConfig(config)
    }
}
