package com.kidplayer.app.domain.model

/**
 * Wrapper for paginated data with metadata about total items and current position
 *
 * @param items The current page of items
 * @param totalCount Total number of items available
 * @param startIndex Starting index of this page
 * @param hasMore Whether there are more items to load
 */
data class PaginatedResult<T>(
    val items: List<T>,
    val totalCount: Int,
    val startIndex: Int,
    val hasMore: Boolean
) {
    /**
     * Calculate if there are more items to load
     */
    companion object {
        fun <T> create(
            items: List<T>,
            totalCount: Int,
            startIndex: Int
        ): PaginatedResult<T> {
            val hasMore = (startIndex + items.size) < totalCount
            return PaginatedResult(
                items = items,
                totalCount = totalCount,
                startIndex = startIndex,
                hasMore = hasMore
            )
        }
    }
}
