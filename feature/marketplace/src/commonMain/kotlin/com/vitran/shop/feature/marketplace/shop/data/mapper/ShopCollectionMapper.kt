package com.vitran.shop.feature.marketplace.shop.data.mapper

import com.vitran.shop.core.domain.pagination.CursorPage
import com.vitran.shop.core.network.pagination.CursorPageDto
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.shop.data.remote.dto.ShopCollectionDetailDataDto
import com.vitran.shop.feature.marketplace.shop.data.remote.dto.ShopCollectionDto
import com.vitran.shop.feature.marketplace.shop.data.remote.dto.ShopCollectionProductDto
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopCollection
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopCollectionDetail
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopCollectionId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopCollectionProduct
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import kotlinx.datetime.Instant

fun ShopCollectionDto.toDomain(): ShopCollection =
    ShopCollection(
        id = ShopCollectionId(id),
        shopId = ShopId(shopId),
        title = title,
        description = description?.takeIf { it.isNotBlank() },
        productCount = productCount,
        createdAt = createdAt?.let { runCatching { Instant.parse(it) }.getOrNull() },
        updatedAt = updatedAt?.let { runCatching { Instant.parse(it) }.getOrNull() },
    )

fun ShopCollectionProductDto.toDomain(): ShopCollectionProduct =
    ShopCollectionProduct(
        id = ProductId(id),
        title = title,
        slug = slug?.takeIf { it.isNotBlank() },
        priceAmount = price,
    )

fun CursorPageDto<ShopCollectionDto>.toShopCollectionPage(): CursorPage<ShopCollection> =
    CursorPage(
        items = results.map { it.toDomain() },
        nextCursor = nextCursor,
        hasMore = hasMore,
    )

fun CursorPageDto<ShopCollectionProductDto>.toShopCollectionProductPage(): CursorPage<ShopCollectionProduct> =
    CursorPage(
        items = results.map { it.toDomain() },
        nextCursor = nextCursor,
        hasMore = hasMore,
    )

fun ShopCollectionDetailDataDto.toDetail(): ShopCollectionDetail =
    ShopCollectionDetail(
        collection = collection.toDomain(),
        products = products.toShopCollectionProductPage(),
    )
