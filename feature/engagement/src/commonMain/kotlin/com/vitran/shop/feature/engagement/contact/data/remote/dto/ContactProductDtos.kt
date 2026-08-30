package com.vitran.shop.feature.engagement.contact.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContactProductDataDto(
    val contact: ContactRouteDto,
    val intent: PurchaseIntentDto,
)

@Serializable
data class ContactRouteDto(
    @SerialName("routed_via")
    val routedVia: String,
    @SerialName("whatsapp_link")
    val whatsappLink: String? = null,
)

@Serializable
data class PurchaseIntentDto(
    val id: Long,
    @SerialName("product_id")
    val productId: Long,
    @SerialName("shop_id")
    val shopId: Long,
    @SerialName("routed_via")
    val routedVia: String,
)
