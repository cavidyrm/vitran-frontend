package com.vitran.shop.core.domain.pagination

/**
 * Transport-independent offset/page pagination result.
 */
data class PageResult<T>(
    val items: List<T>,
    val page: Int,
    val perPage: Int,
    val lastPage: Int,
    val total: Long,
    val hasMore: Boolean,
)
