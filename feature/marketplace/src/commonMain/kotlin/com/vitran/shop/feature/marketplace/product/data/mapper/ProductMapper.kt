package com.vitran.shop.feature.marketplace.product.data.mapper

import com.vitran.shop.feature.marketplace.common.data.serializer.FlexibleCategorySlugSerializer
import com.vitran.shop.feature.marketplace.product.data.remote.dto.ProductDetailsDto
import com.vitran.shop.feature.marketplace.product.data.remote.dto.ProductImageDto
import com.vitran.shop.feature.marketplace.product.data.remote.dto.ProductListItemDto
import com.vitran.shop.feature.marketplace.product.domain.model.ProductDetails
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.product.domain.model.ProductImage
import com.vitran.shop.feature.marketplace.product.domain.model.coverImage
import com.vitran.shop.feature.marketplace.product.domain.model.ProductSummary
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug
import kotlinx.datetime.Instant

internal fun ProductImageDto.toDomain(): ProductImage =
    ProductImage(id = id, url = url, sortOrder = sortOrder, isMain = isMain)

internal fun List<ProductImageDto>.toDomainImages(mainImage: ProductImageDto?): List<ProductImage> {
    val mapped = map { it.toDomain() }.toMutableList()
    if (mainImage != null && mapped.none { it.id == mainImage.id }) {
        mapped += mainImage.toDomain()
    }
    val coverId = mainImage?.id ?: mapped.firstOrNull { it.isMain }?.id
    val flagged =
        if (coverId == null) {
            mapped
        } else {
            mapped.map { image -> image.copy(isMain = image.id == coverId) }
        }
    return flagged.sortedWith(compareBy<ProductImage> { !it.isMain }.thenBy { it.sortOrder })
}

internal fun ProductListItemDto.toDomain(): ProductSummary {
    val images = images.toDomainImages(mainImage)
    return ProductSummary(
        id = ProductId(id),
        shopId = ShopId(shopId),
        categorySlug = categorySlug?.let { FlexibleCategorySlugSerializer.toDomain(it) },
        title = title,
        priceAmount = price,
        compareAtPriceAmount = compareAtPrice,
        active = active,
        confirmed = confirmed,
        mainImage = mainImage?.toDomain()?.copy(isMain = true) ?: images.coverImage(),
        images = images,
    )
}

internal fun ProductDetailsDto.toDomain(): ProductDetails {
    val resolvedImages = images.toDomainImages(mainImage)
    return ProductDetails(
        id = ProductId(id),
        shopId = ShopId(shopId),
        categorySlug = categorySlug?.let { FlexibleCategorySlugSerializer.toDomain(it) },
        title = title,
        description = description,
        priceAmount = price,
        compareAtPriceAmount = compareAtPrice,
        active = active,
        confirmed = confirmed,
        mainImage = mainImage?.toDomain()?.copy(isMain = true) ?: resolvedImages.coverImage(),
        images = resolvedImages,
        createdAt = Instant.parse(createdAt),
        updatedAt = Instant.parse(updatedAt),
    )
}

internal fun com.vitran.shop.core.network.pagination.CursorPageDto<ProductListItemDto>.toProductSummaryPage():
    com.vitran.shop.core.domain.pagination.CursorPage<ProductSummary> =
    com.vitran.shop.core.domain.pagination.CursorPage(
        items = results.map { it.toDomain() },
        nextCursor = nextCursor,
        hasMore = hasMore,
    )
