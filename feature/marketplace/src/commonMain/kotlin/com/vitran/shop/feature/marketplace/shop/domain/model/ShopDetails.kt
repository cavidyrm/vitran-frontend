package com.vitran.shop.feature.marketplace.shop.domain.model

import com.vitran.shop.feature.location.domain.model.CityId
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug
import kotlinx.datetime.Instant

data class ShopDetails(
    val id: ShopId,
    val ownerId: Long,
    val cityId: CityId,
    val title: String,
    val slug: ShopSlug,
    val type: String,
    val shareUrl: String,
    val active: Boolean,
    val confirmed: Boolean,
    val categorySlugs: List<CategorySlug>,
    val createdAt: Instant,
    val updatedAt: Instant,
)
