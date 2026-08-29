package com.vitran.shop.feature.marketplace.shop.domain.model

import kotlin.jvm.JvmInline

@JvmInline
value class ShopId(val value: Long)

@JvmInline
value class ShopSlug(val value: String)

fun ShopId.toNavigationKey(): String = value.toString()

fun ShopSlug.toNavigationKey(): String = value

fun parseShopNavigationKey(key: String): Pair<ShopId?, ShopSlug?> {
    val numeric = key.toLongOrNull()
    return if (numeric != null) ShopId(numeric) to null else null to ShopSlug(key)
}
