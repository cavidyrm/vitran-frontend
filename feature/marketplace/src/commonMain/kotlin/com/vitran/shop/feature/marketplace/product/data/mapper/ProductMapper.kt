package com.vitran.shop.feature.marketplace.product.data.mapper

import com.vitran.shop.feature.marketplace.common.data.serializer.FlexibleCategorySlugSerializer
import com.vitran.shop.feature.marketplace.product.data.remote.dto.ProductDetailsDto
import com.vitran.shop.feature.marketplace.product.data.remote.dto.ProductImageDto
import com.vitran.shop.feature.marketplace.product.data.remote.dto.ProductListItemDto
import com.vitran.shop.feature.marketplace.product.domain.model.ProductDetails
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.product.domain.model.ProductImage
import com.vitran.shop.feature.marketplace.product.domain.model.ProductSummary
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug
import kotlinx.datetime.Instant

internal fun ProductImageDto.toDomain(): ProductImage =
    ProductImage(id = id, url = url, sortOrder = sortOrder)

internal fun ProductListItemDto.toDomain(): ProductSummary =
    ProductSummary(
        id = ProductId(id),
        shopId = ShopId(shopId),
        categorySlug = categorySlug?.let { FlexibleCategorySlugSerializer.toDomain(it) },
        title = title,
        priceAmount = price,
        active = active,
        confirmed = confirmed,
        images = images.sortedBy { it.sortOrder }.map { it.toDomain() },
    )

internal fun ProductDetailsDto.toDomain(): ProductDetails =
    ProductDetails(
        id = ProductId(id),
        shopId = ShopId(shopId),
        categorySlug = categorySlug?.let { FlexibleCategorySlugSerializer.toDomain(it) },
        title = title,
        description = description,
        priceAmount = price,
        active = active,
        confirmed = confirmed,
        images = images.sortedBy { it.sortOrder }.map { it.toDomain() },
        createdAt = Instant.parse(createdAt),
        updatedAt = Instant.parse(updatedAt),
    )

internal fun com.vitran.shop.core.network.pagination.CursorPageDto<ProductListItemDto>.toProductSummaryPage():
    com.vitran.shop.core.domain.pagination.CursorPage<ProductSummary> =
    com.vitran.shop.core.domain.pagination.CursorPage(
        items = results.map { it.toDomain() },
        nextCursor = nextCursor,
        hasMore = hasMore,
    )
