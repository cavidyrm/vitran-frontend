package com.vitran.shop.feature.seller.shop.domain.model

sealed class FulfillmentMode {
    data object Manual : FulfillmentMode()
    data object Redirect : FulfillmentMode()
    data object Api : FulfillmentMode()
    data object Marketplace : FulfillmentMode()
    data object Local : FulfillmentMode()
    data class Unknown(val rawValue: String) : FulfillmentMode()

    fun wireValue(): String =
        when (this) {
            Manual -> "manual"
            Redirect -> "redirect"
            Api -> "api"
            Marketplace -> "marketplace"
            Local -> "local"
            is Unknown -> rawValue
        }

    companion object {
        fun fromRaw(value: String): FulfillmentMode =
            when (value.lowercase()) {
                "manual" -> Manual
                "redirect" -> Redirect
                "api" -> Api
                "marketplace" -> Marketplace
                "local" -> Local
                else -> Unknown(value)
            }
    }
}
