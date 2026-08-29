package com.vitran.shop.feature.auth.data.mapper

/** Maps UI/mobile display format to Postman transport format (e.g. 9123456789). */
internal fun String.toApiPhone(): String {
    val digits = filter { it.isDigit() }
    return when {
        digits.startsWith("98") && digits.length > 10 -> digits.drop(2).trimStart('0')
        digits.startsWith("0") -> digits.drop(1)
        else -> digits
    }
}
