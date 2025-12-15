package com.kidplayer.app.domain.usecase

import com.kidplayer.app.domain.model.ParentalControls
import com.kidplayer.app.domain.repository.ParentalControlsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting parental controls configuration
 */
class GetParentalControlsUseCase @Inject constructor(
    private val repository: ParentalControlsRepository
) {
    operator fun invoke(): Flow<ParentalControls> {
        return repository.getParentalControls()
    }
}
