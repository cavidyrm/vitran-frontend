package com.vitran.shop.feature.marketplace.shop.domain.query

import com.vitran.shop.feature.marketplace.common.domain.filter.CityFilter
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug
import com.vitran.shop.core.domain.pagination.CursorPagination

data class ShopListQuery(
    val city: CityFilter? = null,
    val categorySlug: CategorySlug? = null,
    val pagination: CursorPagination = CursorPagination(),
)

data class ShopBrowseQuery(
    val city: CityFilter? = null,
    val categorySlug: CategorySlug? = null,
    val pagination: CursorPagination = CursorPagination(),
)
