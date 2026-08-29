package com.vitran.shop.feature.marketplace.product.domain.model

import kotlin.jvm.JvmInline

@JvmInline
value class ProductId(val value: Long)

fun ProductId.toNavigationKey(): String = value.toString()
