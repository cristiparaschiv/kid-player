package com.kidplayer.app.domain.usecase

import com.kidplayer.app.domain.model.PinVerificationResult
import com.kidplayer.app.domain.repository.ParentalControlsRepository
import javax.inject.Inject

/**
 * Use case for verifying parent PIN
 */
class VerifyParentPinUseCase @Inject constructor(
    private val repository: ParentalControlsRepository
) {
    suspend operator fun invoke(pin: String): PinVerificationResult {
        return repository.verifyPin(pin)
    }
}
