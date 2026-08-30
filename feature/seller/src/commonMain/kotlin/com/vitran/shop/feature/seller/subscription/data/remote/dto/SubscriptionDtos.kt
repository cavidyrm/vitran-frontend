package com.vitran.shop.feature.seller.subscription.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SubscriptionDataDto(
    val subscription: ShopSubscriptionDto,
)

@Serializable
internal data class ShopSubscriptionDto(
    @SerialName("shop_id") val shopId: Long,
    val plan: SubscriptionPlanDto,
    val status: String,
    @SerialName("started_at") val startedAt: String,
    @SerialName("expires_at") val expiresAt: String? = null,
    @SerialName("days_remaining") val daysRemaining: Int? = null,
)

/**
 * Subscription-embedded plan projection — distinct from public Plan detail DTO.
 */
@Serializable
internal data class SubscriptionPlanDto(
    val id: Long,
    val slug: String,
    val title: String,
    @SerialName("price_amount") val priceAmount: Long? = null,
    @SerialName("duration_days") val durationDays: Int? = null,
    @SerialName("max_products") val maxProducts: Int,
    @SerialName("max_images") val maxImages: Int,
    @SerialName("max_shops") val maxShops: Int,
    val active: Boolean? = null,
    @SerialName("sort_order") val sortOrder: Int? = null,
)

@Serializable
internal data class PurchasePlanRequestDto(
    @SerialName("plan_id") val planId: Long,
)

@Serializable
internal data class PurchaseDataDto(
    val payment: PaymentSessionDto,
)

@Serializable
internal data class PaymentSessionDto(
    @SerialName("payment_id") val paymentId: Long,
    val authority: String,
    @SerialName("payment_url") val paymentUrl: String,
)
