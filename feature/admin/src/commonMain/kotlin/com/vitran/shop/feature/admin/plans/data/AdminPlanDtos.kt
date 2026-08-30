package com.vitran.shop.feature.admin.plans.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
internal data class AdminPlanDto(
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

@Serializable
internal data class AdminPlansDataDto(val plans: List<AdminPlanDto> = emptyList())

@Serializable
internal data class AdminPlanDataDto(val plan: AdminPlanDto)

@Serializable
internal data class CreatePlanRequestDto(
    val slug: String,
    val title: String,
    val description: String? = null,
    @SerialName("price_amount") val priceAmount: Long,
    @SerialName("duration_days") val durationDays: Int? = null,
    @SerialName("max_products") val maxProducts: Int,
    @SerialName("max_images") val maxImages: Int,
    @SerialName("max_shops") val maxShops: Int,
    val features: JsonObject,
    val active: Boolean,
    @SerialName("sort_order") val sortOrder: Int,
)
