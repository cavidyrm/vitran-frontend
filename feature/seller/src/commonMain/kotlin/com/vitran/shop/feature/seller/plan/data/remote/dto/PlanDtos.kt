package com.vitran.shop.feature.seller.plan.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
internal data class PlansListDataDto(
    val plans: List<PublicPlanListItemDto> = emptyList(),
)

@Serializable
internal data class PlanDetailDataDto(
    val plan: PublicPlanDetailDto,
)

@Serializable
internal data class PublicPlanListItemDto(
    val id: Long,
    val slug: String,
    val title: String,
    @SerialName("price_amount") val priceAmount: Long,
    @SerialName("duration_days") val durationDays: Int? = null,
    @SerialName("max_products") val maxProducts: Int,
    @SerialName("max_images") val maxImages: Int,
    @SerialName("max_shops") val maxShops: Int,
    val features: JsonObject = JsonObject(emptyMap()),
    val active: Boolean = true,
    @SerialName("sort_order") val sortOrder: Int = 0,
)

@Serializable
internal data class PublicPlanDetailDto(
    val id: Long,
    val slug: String,
    val title: String,
    val description: String? = null,
    @SerialName("price_amount") val priceAmount: Long,
    @SerialName("duration_days") val durationDays: Int? = null,
    @SerialName("max_products") val maxProducts: Int,
    @SerialName("max_images") val maxImages: Int,
    @SerialName("max_shops") val maxShops: Int,
    val features: JsonObject = JsonObject(emptyMap()),
    val active: Boolean = true,
    @SerialName("sort_order") val sortOrder: Int = 0,
)
