package com.vitran.shop.feature.marketplace.shop.domain.error

import com.vitran.shop.core.domain.error.AppError

fun AppError.toShopResultError(): AppError =
    if (this is AppError.NotFound) {
        AppError.NotFound(message ?: "فروشگاه در دسترس نیست")
    } else {
        this
    }
