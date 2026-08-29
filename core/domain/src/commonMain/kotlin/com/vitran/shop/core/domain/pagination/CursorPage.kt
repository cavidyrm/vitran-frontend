package com.vitran.shop.core.domain.pagination

/**
 * Transport-independent cursor pagination result.
 */
data class CursorPage<T>(
    val items: List<T>,
    val nextCursor: String?,
    val hasMore: Boolean,
)
