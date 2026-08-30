package com.vitran.shop.feature.seller.shop.domain.model

import com.vitran.shop.feature.location.domain.model.CityId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopSlug
import kotlinx.datetime.Instant

/**
 * Owner projection of a shop. Fields beyond the minimal GET-by-ID set are nullable
 * because Postman examples differ by endpoint (create vs get vs update).
 */
data class SellerShopDetails(
    val id: ShopId,
    val slug: ShopSlug,
    val active: Boolean,
    val confirmed: Boolean,
    val publicationState: ShopPublicationState =
        shopPublicationState(active = active, confirmed = confirmed),
    val ownerId: Long? = null,
    val cityId: CityId? = null,
    val title: String? = null,
    val description: String? = null,
    val address: String? = null,
    val phoneNumber: String? = null,
    val supportTimes: String? = null,
    val type: String? = null,
    val shareUrl: String? = null,
    val qrCodeUrl: String? = null,
    /** Opaque numeric values from seller `category_slugs` transport (Postman). */
    val categoryNumericIds: List<Long> = emptyList(),
    val whatsapp: String? = null,
    val telegram: String? = null,
    val instagram: String? = null,
    val website: String? = null,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null,
)
