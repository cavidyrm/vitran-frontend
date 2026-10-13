package com.vitran.shop.feature.marketplace.shop.domain.query

import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopCollectionId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId

data class ShopCollectionListQuery(
    val shopId: ShopId,
    val pagination: CursorPagination = CursorPagination(),
)

data class ShopCollectionDetailQuery(
    val shopId: ShopId,
    val collectionId: ShopCollectionId,
    val pagination: CursorPagination = CursorPagination(),
)
