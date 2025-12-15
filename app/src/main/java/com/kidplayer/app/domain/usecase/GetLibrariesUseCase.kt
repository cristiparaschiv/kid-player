package com.kidplayer.app.domain.usecase

import com.kidplayer.app.domain.model.Library
import com.kidplayer.app.domain.model.Result
import com.kidplayer.app.domain.repository.JellyfinRepository
import javax.inject.Inject

/**
 * Use case to get available media libraries from Jellyfin
 * Filters to return only video libraries
 */
class GetLibrariesUseCase @Inject constructor(
    private val repository: JellyfinRepository
) {
    /**
     * Get all available libraries
     *
     * @param videoOnly If true, only return video libraries
     * @return Result containing list of libraries or error
     */
    suspend operator fun invoke(videoOnly: Boolean = true): Result<List<Library>> {
        return when (val result = repository.getLibraries()) {
            is Result.Success -> {
                val libraries = if (videoOnly) {
                    result.data.filter { it.isVideoLibrary() }
                } else {
                    result.data
                }
                Result.Success(libraries)
            }
            is Result.Error -> result
            is Result.Loading -> Result.Loading
        }
    }
}
