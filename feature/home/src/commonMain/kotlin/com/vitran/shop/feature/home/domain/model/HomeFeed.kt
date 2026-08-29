package com.vitran.shop.feature.home.domain.model

import com.vitran.shop.feature.location.domain.model.CityId

/**
 * Aggregated home feed. Item schemas are not mapped until backend contract is verified.
 */
data class HomeFeed(
    val cityId: CityId?,
    val featuredCount: Int,
    val popularCount: Int,
    val categoriesCount: Int,
    val followingCount: Int,
    val personalCount: Int,
) {
    val hasAnySection: Boolean =
        featuredCount + popularCount + categoriesCount + followingCount + personalCount > 0

    /** True when non-empty item DTOs exist and UI can bind real section data. */
    val itemsVerified: Boolean = false
}
