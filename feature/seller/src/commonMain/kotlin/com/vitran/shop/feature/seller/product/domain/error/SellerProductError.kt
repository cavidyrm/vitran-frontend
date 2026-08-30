package com.vitran.shop.feature.seller.product.domain.error

import com.vitran.shop.core.domain.error.AppError

fun AppError.toSellerProductAppError(): AppError =
    when {
        this is AppError.NotFound || this is AppError.Forbidden ->
            AppError.NotFound(
                message = message ?: "محصول در دسترس نیست",
                httpStatus = httpStatus,
                backendCode = backendCode,
                fieldErrors = fieldErrors,
            )
        isProductNotApproved() ->
            AppError.Validation(
                message = message ?: "محصول باید ابتدا تأیید شود",
                httpStatus = httpStatus,
                backendCode = backendCode,
                fieldErrors = fieldErrors,
            )
        else -> this
    }

/**
 * Heuristic mapping when backend rejects publish because product is unconfirmed.
 * Prefer field/reason signals over generic 400/403.
 */
fun AppError.isProductNotApproved(): Boolean {
    val haystack =
        buildList {
            message?.let { add(it.lowercase()) }
            fieldErrors.forEach { err ->
                add(err.reason.lowercase())
                err.messages.forEach { add(it.lowercase()) }
            }
        }
    return haystack.any { text ->
        text.contains("confirmed") ||
            text.contains("not approved") ||
            text.contains("approval") ||
            text.contains("تأیید") ||
            text.contains("تایید")
    }
}
