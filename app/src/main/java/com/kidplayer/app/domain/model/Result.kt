package com.kidplayer.app.domain.model

/**
 * A generic wrapper for data that can represent success or failure states.
 * This provides a type-safe way to handle operations that can fail.
 */
sealed class Result<out T> {
    /**
     * Success state containing the data
     */
    data class Success<T>(val data: T) : Result<T>()

    /**
     * Error state containing an error message
     */
    data class Error(val message: String, val exception: Throwable? = null) : Result<Nothing>()

    /**
     * Loading state for async operations
     */
    object Loading : Result<Nothing>()

    /**
     * Returns true if this is a Success state
     */
    val isSuccess: Boolean
        get() = this is Success

    /**
     * Returns true if this is an Error state
     */
    val isError: Boolean
        get() = this is Error

    /**
     * Returns the data if Success, null otherwise
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    /**
     * Returns the error message if Error, null otherwise
     */
    fun getErrorMessage(): String? = when (this) {
        is Error -> message
        else -> null
    }
}
