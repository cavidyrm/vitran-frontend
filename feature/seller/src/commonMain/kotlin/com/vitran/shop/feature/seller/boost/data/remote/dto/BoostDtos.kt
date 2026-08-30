package com.vitran.shop.feature.seller.boost.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
internal data class CreateBoostRequestDto(
    @SerialName("product_id") val productId: Long? = null,
    val days: Int,
    @SerialName("price_paid") val pricePaid: Long,
)

@Serializable
internal data class CreateBoostDataDto(
    val boost: CreatedBoostDto,
)

@Serializable
internal data class CreatedBoostDto(
    val id: Long,
    @SerialName("shop_id") val shopId: Long,
    val days: Int,
)

@Serializable
internal data class ActiveBoostsDataDto(
    val boosts: List<JsonElement> = emptyList(),
)
