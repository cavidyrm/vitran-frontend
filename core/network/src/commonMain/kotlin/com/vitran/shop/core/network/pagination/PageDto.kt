package com.vitran.shop.core.network.pagination

import com.vitran.shop.core.domain.pagination.PageResult
import com.vitran.shop.core.network.model.ApiEnvelope
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PageDto<T>(
    val page: Int? = null,
    @SerialName("per_page")
    val perPage: Int,
    @SerialName("last_page")
    val lastPage: Int? = null,
    val from: Int? = null,
    val to: Int? = null,
    val total: Long? = null,
    @SerialName("has_more")
    val hasMore: Boolean,
    val results: List<T> = emptyList(),
)

fun <T> PageDto<T>.toDomain(): PageResult<T> = toDomain(transform = { it })

fun <T, R> PageDto<T>.toDomain(transform: (T) -> R): PageResult<R> {
    val resolvedPage = resolvedPage()
    return PageResult(
        items = results.map(transform),
        page = resolvedPage,
        perPage = perPage,
        lastPage = resolvedLastPage(resolvedPage),
        total = resolvedTotal(resolvedPage),
        hasMore = hasMore,
    )
}

private fun <T> PageDto<T>.resolvedPage(): Int = page?.takeIf { it > 0 } ?: 1

private fun <T> PageDto<T>.resolvedLastPage(resolvedPage: Int): Int =
    lastPage?.takeIf { it > 0 } ?: if (hasMore) resolvedPage + 1 else resolvedPage

private fun <T> PageDto<T>.resolvedTotal(resolvedPage: Int): Long {
    total?.let { return it }
    val seen = ((resolvedPage - 1).coerceAtLeast(0) * perPage) + results.size
    return if (hasMore) (seen + 1).toLong() else seen.toLong()
}

/** Envelope whose `data` is a page result. */
typealias PageEnvelope<T> = ApiEnvelope<PageDto<T>>
