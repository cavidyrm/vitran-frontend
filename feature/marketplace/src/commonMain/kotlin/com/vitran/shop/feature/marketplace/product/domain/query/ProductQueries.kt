package com.vitran.shop.feature.marketplace.product.domain.query

import com.vitran.shop.feature.marketplace.common.domain.filter.CityFilter
import com.vitran.shop.feature.marketplace.common.domain.filter.ShopFilter
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug
import com.vitran.shop.core.domain.pagination.CursorPagination

data class ProductBrowseQuery(
    val city: CityFilter? = null,
    val categorySlug: CategorySlug? = null,
    val shop: ShopFilter? = null,
    val pagination: CursorPagination = CursorPagination(),
)

data class ProductSearchQuery(
    val query: String,
    val city: CityFilter? = null,
    val categorySlug: CategorySlug? = null,
    val shop: ShopFilter? = null,
    val pagination: CursorPagination = CursorPagination(),
)
