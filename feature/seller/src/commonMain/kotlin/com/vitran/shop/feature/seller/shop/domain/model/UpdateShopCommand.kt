package com.vitran.shop.feature.seller.shop.domain.model

import com.vitran.shop.feature.location.domain.model.CityId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopSlug

/**
 * Domain update command. Null optional strings mean "leave unchanged" until PATCH
 * clearing semantics are verified from backend source (documented as Open gap).
 */
data class UpdateShopCommand(
    val shopId: ShopId,
    val title: String? = null,
    val slug: ShopSlug? = null,
    val description: String? = null,
    val address: String? = null,
    val phoneNumber: String? = null,
    val supportTimes: String? = null,
    val type: String? = null,
    val cityId: CityId? = null,
    val categoryNumericIds: List<Long>? = null,
    val whatsapp: String? = null,
    val telegram: String? = null,
    val instagram: String? = null,
    val website: String? = null,
)
