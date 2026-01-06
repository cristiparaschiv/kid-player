package com.kidplayer.app.domain.usecase

import com.kidplayer.app.domain.model.Playlist
import com.kidplayer.app.domain.model.Result
import com.kidplayer.app.domain.repository.JellyfinRepository
import javax.inject.Inject

/**
 * Use case to get user's playlists from Jellyfin
 */
class GetPlaylistsUseCase @Inject constructor(
    private val repository: JellyfinRepository
) {
    /**
     * Get all available playlists
     *
     * @return Result containing list of playlists or error
     */
    suspend operator fun invoke(): Result<List<Playlist>> {
        return repository.getPlaylists()
    }
}
