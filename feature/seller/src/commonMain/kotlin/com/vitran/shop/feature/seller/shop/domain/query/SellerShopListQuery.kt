package com.vitran.shop.feature.seller.shop.domain.query

import com.vitran.shop.core.domain.pagination.CursorPagination

sealed class SellerShopFilter {
    data object All : SellerShopFilter()
    data object Active : SellerShopFilter()
    data object Inactive : SellerShopFilter()
}

data class SellerShopListQuery(
    val activeFilter: SellerShopFilter = SellerShopFilter.All,
    val pagination: CursorPagination = CursorPagination(),
)
