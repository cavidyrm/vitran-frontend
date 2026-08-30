package com.vitran.shop.feature.seller.shop.domain.error

import com.vitran.shop.core.domain.error.AppError

fun AppError.isSlugAlreadyTaken(): Boolean =
    this is AppError.Conflict &&
        fieldErrors.any { it.reason.equals("slug", ignoreCase = true) }

fun AppError.toSellerShopAppError(): AppError =
    when {
        isSlugAlreadyTaken() -> this
        this is AppError.NotFound || this is AppError.Forbidden ->
            AppError.NotFound(message ?: "فروشگاه در دسترس نیست", httpStatus = httpStatus, backendCode = backendCode, fieldErrors = fieldErrors)
        else -> this
    }
