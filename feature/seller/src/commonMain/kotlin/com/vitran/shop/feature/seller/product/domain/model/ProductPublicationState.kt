package com.vitran.shop.feature.seller.product.domain.model

/**
 * Seller-facing product publication lifecycle derived from backend `active` + `confirmed`.
 * Do not scatter raw boolean checks in Composables.
 */
sealed class ProductPublicationState {
    data object PendingApproval : ProductPublicationState()
    data object Live : ProductPublicationState()
    data object ApprovedHidden : ProductPublicationState()

    /**
     * `active=true` with `confirmed=false` is not a documented live state.
     */
    data object Inconsistent : ProductPublicationState()
}

fun productPublicationState(active: Boolean, confirmed: Boolean): ProductPublicationState =
    when {
        !active && !confirmed -> ProductPublicationState.PendingApproval
        active && confirmed -> ProductPublicationState.Live
        !active && confirmed -> ProductPublicationState.ApprovedHidden
        else -> ProductPublicationState.Inconsistent
    }
