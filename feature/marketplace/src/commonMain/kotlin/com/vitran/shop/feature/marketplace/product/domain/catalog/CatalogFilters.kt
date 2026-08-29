package com.vitran.shop.feature.marketplace.product.domain.catalog

import com.vitran.shop.feature.marketplace.common.domain.filter.CityFilter
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug

data class PriceRange(
    val minimum: Long? = null,
    val maximum: Long? = null,
) {
    init {
        if (minimum != null && maximum != null) {
            require(minimum <= maximum) { "minimum must be <= maximum" }
        }
    }
}

enum class CatalogSort {
    Relevance,
    Newest,
    PriceAscending,
    PriceDescending,
}

enum class MinimumRating(val backendValue: Int) {
    OneAndUp(1),
    TwoAndUp(2),
    ThreeAndUp(3),
    FourAndUp(4),
}

/**
 * Request-side catalog filters. Response mapping deferred until contract verified.
 */
data class CatalogFilters(
    val query: String? = null,
    val city: CityFilter? = null,
    val categories: Set<CategorySlug> = emptySet(),
    val shops: Set<ShopId> = emptySet(),
    val priceRange: PriceRange? = null,
    val minimumRating: MinimumRating? = null,
    val sort: CatalogSort = CatalogSort.Relevance,
    val pagination: com.vitran.shop.core.domain.pagination.CursorPagination =
        com.vitran.shop.core.domain.pagination.CursorPagination(),
)
