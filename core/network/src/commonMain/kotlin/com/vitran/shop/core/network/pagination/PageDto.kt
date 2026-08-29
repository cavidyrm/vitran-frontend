package com.vitran.shop.core.network.pagination

import com.vitran.shop.core.domain.pagination.PageResult
import com.vitran.shop.core.network.model.ApiEnvelope
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PageDto<T>(
    val page: Int,
    @SerialName("per_page")
    val perPage: Int,
    @SerialName("last_page")
    val lastPage: Int,
    val from: Int? = null,
    val to: Int? = null,
    val total: Long,
    @SerialName("has_more")
    val hasMore: Boolean,
    val results: List<T> = emptyList(),
)

fun <T> PageDto<T>.toDomain(): PageResult<T> =
    PageResult(
        items = results,
        page = page,
        perPage = perPage,
        lastPage = lastPage,
        total = total,
        hasMore = hasMore,
    )

/** Envelope whose `data` is a page result. */
typealias PageEnvelope<T> = ApiEnvelope<PageDto<T>>
