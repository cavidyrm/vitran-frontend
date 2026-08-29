package com.vitran.shop.core.domain.pagination

/**
 * Cursor-mode pagination request parameters.
 * Never combine with [PagePagination].
 */
data class CursorPagination(
    val cursor: String? = null,
    val perPage: Int = DEFAULT_PER_PAGE,
) {
    init {
        require(perPage in MIN_PER_PAGE..MAX_PER_PAGE) {
            "perPage must be between $MIN_PER_PAGE and $MAX_PER_PAGE, was $perPage"
        }
    }

    companion object {
        const val DEFAULT_PER_PAGE: Int = 20
        const val MIN_PER_PAGE: Int = 1
        const val MAX_PER_PAGE: Int = 100
    }
}
