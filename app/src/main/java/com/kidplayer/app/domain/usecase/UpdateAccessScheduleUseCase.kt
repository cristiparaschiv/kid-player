package com.kidplayer.app.domain.usecase

import com.kidplayer.app.domain.model.AccessSchedule
import com.kidplayer.app.domain.repository.ParentalControlsRepository
import javax.inject.Inject

/**
 * Use case for updating access schedule configuration
 */
class UpdateAccessScheduleUseCase @Inject constructor(
    private val repository: ParentalControlsRepository
) {
    suspend operator fun invoke(schedule: AccessSchedule?): Result<Unit> {
        return repository.updateAccessSchedule(schedule)
    }
}
