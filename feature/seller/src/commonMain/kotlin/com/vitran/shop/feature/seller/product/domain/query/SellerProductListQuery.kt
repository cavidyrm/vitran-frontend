package com.vitran.shop.feature.seller.product.domain.query

import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug

enum class SellerProductActiveFilter {
    All,
    Active,
    Inactive,
}

data class SellerProductListQuery(
    val shopId: ShopId? = null,
    val activeFilter: SellerProductActiveFilter = SellerProductActiveFilter.All,
    val confirmed: Boolean? = null,
    val categorySlug: CategorySlug? = null,
    val pagination: CursorPagination = CursorPagination(),
)
