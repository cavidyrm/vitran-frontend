package com.vitran.shop.feature.seller.boost.domain.model

/**
 * Active boost list. Non-empty item schema is unverified — do not invent PlacementBoost fields.
 */
sealed class ActiveBoosts {
    data object Empty : ActiveBoosts()
    data class Unmapped(val count: Int) : ActiveBoosts()
}
