package com.vitran.shop.feature.account.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ProductMatchesDataDto(
    val matches: List<ProductMatchDto> = emptyList(),
)

@Serializable
internal data class ProductMatchDto(
    val id: Long,
    @SerialName("product_id") val productId: Long,
    val product: ProductMatchProductDto? = null,
    @SerialName("person_id") val personId: Long? = null,
    val slot: String? = null,
    val reason: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
)

@Serializable
internal data class ProductMatchProductDto(
    val id: Long,
    val title: String? = null,
    val price: Long? = null,
    @SerialName("compare_at_price") val compareAtPrice: Long? = null,
)
