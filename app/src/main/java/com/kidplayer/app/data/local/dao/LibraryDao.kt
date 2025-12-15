package com.kidplayer.app.data.local.dao

import androidx.room.*
import com.kidplayer.app.data.local.entity.LibraryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Library entities
 * Provides CRUD operations for local library caching
 */
@Dao
interface LibraryDao {

    /**
     * Get all libraries for a specific user
     */
    @Query("SELECT * FROM libraries WHERE userId = :userId ORDER BY name ASC")
    fun getAllLibraries(userId: String): Flow<List<LibraryEntity>>

    /**
     * Get enabled libraries only for a specific user
     */
    @Query("SELECT * FROM libraries WHERE userId = :userId AND isEnabled = 1 ORDER BY name ASC")
    fun getEnabledLibraries(userId: String): Flow<List<LibraryEntity>>

    /**
     * Get a specific library by ID and user
     */
    @Query("SELECT * FROM libraries WHERE userId = :userId AND id = :libraryId LIMIT 1")
    suspend fun getLibraryById(userId: String, libraryId: String): LibraryEntity?

    /**
     * Get libraries by collection type for a specific user
     */
    @Query("SELECT * FROM libraries WHERE userId = :userId AND collectionType = :collectionType ORDER BY name ASC")
    fun getLibrariesByType(userId: String, collectionType: String): Flow<List<LibraryEntity>>

    /**
     * Insert a single library
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLibrary(library: LibraryEntity)

    /**
     * Insert multiple libraries
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLibraries(libraries: List<LibraryEntity>)

    /**
     * Update a library
     */
    @Update
    suspend fun updateLibrary(library: LibraryEntity)

    /**
     * Update library enabled status (user-scoped for security)
     */
    @Query("UPDATE libraries SET isEnabled = :isEnabled WHERE userId = :userId AND id = :libraryId")
    suspend fun updateLibraryEnabled(userId: String, libraryId: String, isEnabled: Boolean)

    /**
     * Delete a specific library (user-scoped for security)
     */
    @Query("DELETE FROM libraries WHERE userId = :userId AND id = :libraryId")
    suspend fun deleteLibrary(userId: String, libraryId: String)

    /**
     * Delete all libraries (for all users - use with caution)
     */
    @Query("DELETE FROM libraries")
    suspend fun deleteAllLibraries()

    /**
     * Delete all libraries for a specific user
     */
    @Query("DELETE FROM libraries WHERE userId = :userId")
    suspend fun deleteAllLibrariesForUser(userId: String)

    /**
     * Get count of libraries for a specific user
     */
    @Query("SELECT COUNT(*) FROM libraries WHERE userId = :userId")
    suspend fun getLibraryCount(userId: String): Int
}
