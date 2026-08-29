package com.vitran.shop.core.domain.pagination

/**
 * Offset/page-mode pagination request parameters.
 * Never combine with [CursorPagination].
 */
data class PagePagination(
    val page: Int = DEFAULT_PAGE,
    val perPage: Int = DEFAULT_PER_PAGE,
) {
    init {
        require(page >= MIN_PAGE) { "page must be >= $MIN_PAGE, was $page" }
        require(perPage in MIN_PER_PAGE..MAX_PER_PAGE) {
            "perPage must be between $MIN_PER_PAGE and $MAX_PER_PAGE, was $perPage"
        }
    }

    companion object {
        const val DEFAULT_PAGE: Int = 1
        const val DEFAULT_PER_PAGE: Int = 20
        const val MIN_PAGE: Int = 1
        const val MIN_PER_PAGE: Int = 1
        const val MAX_PER_PAGE: Int = 100
    }
}
