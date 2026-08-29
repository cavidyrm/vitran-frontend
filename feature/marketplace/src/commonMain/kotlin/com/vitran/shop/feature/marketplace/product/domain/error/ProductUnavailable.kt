package com.vitran.shop.feature.marketplace.product.domain.error

import com.vitran.shop.core.domain.error.AppError

fun AppError.toProductResultError(): AppError =
    if (this is AppError.NotFound) {
        AppError.NotFound(message ?: "محصول در دسترس نیست")
    } else {
        this
    }
