package com.kidplayer.app.domain.usecase

import com.kidplayer.app.domain.repository.ParentalControlsRepository
import javax.inject.Inject

/**
 * Use case for setting or updating parent PIN
 */
class SetParentPinUseCase @Inject constructor(
    private val repository: ParentalControlsRepository
) {
    suspend operator fun invoke(pin: String): Result<Unit> {
        return repository.setPin(pin)
    }
}
