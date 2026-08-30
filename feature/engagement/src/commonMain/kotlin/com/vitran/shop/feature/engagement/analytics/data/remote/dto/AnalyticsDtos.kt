package com.vitran.shop.feature.engagement.analytics.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserEventRequestDto(
    @SerialName("event_type")
    val eventType: String,
    @SerialName("session_id")
    val sessionId: String,
    @SerialName("product_id")
    val productId: Long? = null,
    @SerialName("shop_id")
    val shopId: Long? = null,
)

@Serializable
data class UserEventDataDto(
    val event: UserEventEchoDto,
)

@Serializable
data class UserEventEchoDto(
    val id: Long,
    @SerialName("event_type")
    val eventType: String,
    @SerialName("product_id")
    val productId: Long? = null,
)

@Serializable
data class ShopAnalyticsEventRequestDto(
    val event: String,
    @SerialName("session_id")
    val sessionId: String,
    @SerialName("product_id")
    val productId: Long? = null,
    @SerialName("category_slug")
    val categorySlug: String? = null,
    @SerialName("city_id")
    val cityId: Long? = null,
)
