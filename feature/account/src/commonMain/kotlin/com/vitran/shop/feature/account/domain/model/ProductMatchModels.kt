package com.vitran.shop.feature.account.domain.model

import kotlinx.datetime.Instant

data class ProductMatch(
    val id: Long,
    val productId: Long,
    val product: ProductMatchProduct?,
    val personId: PersonId?,
    val slot: String?,
    val reason: ProductMatchReason,
    val createdAt: Instant?,
)

data class ProductMatchProduct(
    val id: Long,
    val title: String?,
    val priceAmount: Long?,
    val compareAtPriceAmount: Long?,
)

sealed interface ProductMatchReason {
    data object New : ProductMatchReason
    data object Discount : ProductMatchReason
    data class Unknown(val raw: String) : ProductMatchReason
}
