package com.vitran.shop.feature.seller.shop.domain.model

/**
 * Seller-facing publication lifecycle derived from backend `active` + `confirmed`.
 */
sealed class ShopPublicationState {
    data object PendingApproval : ShopPublicationState()
    data object Live : ShopPublicationState()
    data object ApprovedHidden : ShopPublicationState()

    /**
     * `active=true` with `confirmed=false` is not a documented live state.
     * Keep the app running and surface inconsistency for diagnostics.
     */
    data object Inconsistent : ShopPublicationState()
}

fun shopPublicationState(active: Boolean, confirmed: Boolean): ShopPublicationState =
    when {
        !active && !confirmed -> ShopPublicationState.PendingApproval
        active && confirmed -> ShopPublicationState.Live
        !active && confirmed -> ShopPublicationState.ApprovedHidden
        else -> ShopPublicationState.Inconsistent
    }
