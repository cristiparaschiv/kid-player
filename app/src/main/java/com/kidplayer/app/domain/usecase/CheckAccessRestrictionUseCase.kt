package com.kidplayer.app.domain.usecase

import com.kidplayer.app.domain.model.AccessRestriction
import com.kidplayer.app.domain.repository.ParentalControlsRepository
import javax.inject.Inject

/**
 * Use case for checking if app access is currently allowed
 */
class CheckAccessRestrictionUseCase @Inject constructor(
    private val repository: ParentalControlsRepository
) {
    suspend operator fun invoke(): AccessRestriction {
        return repository.checkAccessRestriction()
    }
}
