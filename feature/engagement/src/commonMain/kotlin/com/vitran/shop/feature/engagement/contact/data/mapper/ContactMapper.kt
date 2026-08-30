package com.vitran.shop.feature.engagement.contact.data.mapper

import com.vitran.shop.feature.engagement.contact.data.remote.dto.ContactProductDataDto
import com.vitran.shop.feature.engagement.contact.data.remote.dto.ContactRouteDto
import com.vitran.shop.feature.engagement.contact.domain.model.ContactProductResult
import com.vitran.shop.feature.engagement.contact.domain.model.ContactRoute
import com.vitran.shop.feature.engagement.contact.domain.model.PurchaseIntent
import com.vitran.shop.feature.engagement.contact.domain.model.PurchaseIntentId
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId

internal fun ContactProductDataDto.toDomain(): ContactProductResult {
    val route = contact.toDomain()
    return ContactProductResult(
        route = route,
        intent = PurchaseIntent(
            id = PurchaseIntentId(intent.id),
            productId = ProductId(intent.productId),
            shopId = ShopId(intent.shopId),
            route = mapRoute(intent.routedVia, whatsappLink = contact.whatsappLink),
        ),
    )
}

internal fun ContactRouteDto.toDomain(): ContactRoute =
    mapRoute(routedVia, whatsappLink)

internal fun mapRoute(routedVia: String, whatsappLink: String?): ContactRoute =
    when (routedVia.lowercase()) {
        "whatsapp" -> {
            val link = whatsappLink
            if (link.isNullOrBlank()) {
                ContactRoute.Unsupported(routedVia)
            } else {
                ContactRoute.WhatsApp(link)
            }
        }
        else -> ContactRoute.Unsupported(routedVia)
    }
