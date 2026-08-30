package com.vitran.shop.feature.seller.shop.domain.model

sealed class FulfillmentMode {
    data object Manual : FulfillmentMode()
    data object Redirect : FulfillmentMode()
    data class Unknown(val rawValue: String) : FulfillmentMode()

    companion object {
        fun fromRaw(value: String): FulfillmentMode =
            when (value.lowercase()) {
                "manual" -> Manual
                "redirect" -> Redirect
                else -> Unknown(value)
            }
    }
}
