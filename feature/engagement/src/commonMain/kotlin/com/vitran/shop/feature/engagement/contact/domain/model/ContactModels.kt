package com.vitran.shop.feature.engagement.contact.domain.model

import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import kotlin.jvm.JvmInline

@JvmInline
value class PurchaseIntentId(val value: Long)

sealed interface ContactRoute {
    data class WhatsApp(val url: String) : ContactRoute

    data class Unsupported(val rawType: String) : ContactRoute
}

data class PurchaseIntent(
    val id: PurchaseIntentId,
    val productId: ProductId,
    val shopId: ShopId,
    val route: ContactRoute,
)

data class ContactProductResult(
    val route: ContactRoute,
    val intent: PurchaseIntent,
)
