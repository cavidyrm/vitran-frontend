package com.vitran.shop.core.network.pagination

import com.vitran.shop.core.domain.pagination.CursorPage
import com.vitran.shop.core.network.model.ApiEnvelope
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CursorPageDto<T>(
    @SerialName("per_page")
    val perPage: Int,
    @SerialName("has_more")
    val hasMore: Boolean,
    @SerialName("next_cursor")
    val nextCursor: String? = null,
    val results: List<T> = emptyList(),
)

fun <T> CursorPageDto<T>.toDomain(): CursorPage<T> =
    CursorPage(
        items = results,
        nextCursor = nextCursor,
        hasMore = hasMore,
    )

/** Envelope whose `data` is a cursor page. */
typealias CursorPageEnvelope<T> = ApiEnvelope<CursorPageDto<T>>
