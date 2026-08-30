package com.vitran.shop.feature.seller.shop.domain.model

/**
 * Ephemeral shop API credential. Never log, persist, or put in navigation.
 */
class ShopApiKey private constructor(
    private val value: String,
) {
    fun reveal(): String = value

    override fun equals(other: Any?): Boolean =
        other is ShopApiKey && other.value == value

    override fun hashCode(): Int = value.hashCode()

    override fun toString(): String = "ShopApiKey(***)"

    companion object {
        fun of(value: String): ShopApiKey = ShopApiKey(value)
    }
}
