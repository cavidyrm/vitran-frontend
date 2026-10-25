package com.vitran.shop.feature.admin.moderation.data

import com.vitran.shop.feature.admin.moderation.domain.AdminShopSummary
import com.vitran.shop.feature.admin.moderation.domain.withStoredFieldsFrom
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId

/**
 * Admin shop review has list and confirm only — no GET by id.
 * Detail reads the row last seen from either call.
 */
class AdminShopModerationCache {
    private val shops = mutableMapOf<Long, AdminShopSummary>()

    fun find(id: ShopId): AdminShopSummary? = shops[id.value]

    fun remember(shop: AdminShopSummary): AdminShopSummary {
        val merged = shops[shop.id.value]?.let { previous -> shop.withStoredFieldsFrom(previous) } ?: shop
        shops[shop.id.value] = merged
        return merged
    }

    fun rememberAll(shops: List<AdminShopSummary>) {
        shops.forEach { remember(it) }
    }
}
