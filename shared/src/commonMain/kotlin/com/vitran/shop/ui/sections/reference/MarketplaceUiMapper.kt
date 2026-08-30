package com.vitran.shop.ui.sections.reference

import com.vitran.shop.feature.marketplace.product.domain.model.ProductDetails
import com.vitran.shop.feature.marketplace.product.domain.model.ProductSummary
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopDetails
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopSummary
import com.vitran.shop.ui.sections.categories.CategoriesMerchantShop
import com.vitran.shop.ui.sections.categories.CategoriesProduct
import com.vitran.shop.feature.engagement.review.domain.model.ProductReview
import com.vitran.shop.ui.sections.product.ProductDetailMedia
import com.vitran.shop.ui.sections.product.ProductDetailMerchant
import com.vitran.shop.ui.sections.product.ProductDetailMock
import com.vitran.shop.ui.sections.product.ProductReviewHistogram
import com.vitran.shop.ui.sections.product.ProductReviewItem
import com.vitran.shop.ui.sections.product.ProductReviewsMock
import com.vitran.shop.ui.sections.product.productSlug
import com.vitran.shop.ui.sections.store.StoreMock
import com.vitran.shop.ui.theme.ShopPurple

private const val PLACEHOLDER_IMAGE =
    "https://cdn.shopify.com/s/files/1/0533/2089/files/placeholder-images-image_large.png?width=800"

fun formatMarketplacePrice(amount: Long): String {
    val digits = amount.toString().reversed().chunked(3).joinToString("٬").reversed()
    return "$digits تومان"
}

fun ProductSummary.toCategoriesProduct(storeName: String = "فروشگاه"): CategoriesProduct {
    val imageUrl = images.firstOrNull()?.url ?: PLACEHOLDER_IMAGE
    return CategoriesProduct(
        id = id.value.toString(),
        storeName = storeName,
        title = title,
        imageUrl = imageUrl,
        rating = null,
        reviewCountLabel = null,
        priceLabel = formatMarketplacePrice(priceAmount),
    )
}

fun ProductDetails.toProductDetailMock(shopTitle: String = "فروشگاه"): ProductDetailMock {
    val imageUrls = images.map { it.url }.ifEmpty { listOf(PLACEHOLDER_IMAGE) }
    return ProductDetailMock(
        id = id.value.toString(),
        slug = productSlug(title),
        title = title,
        media = ProductDetailMedia(imageUrls = imageUrls),
        merchant = ProductDetailMerchant(
            name = shopTitle,
            logoUrl = PLACEHOLDER_IMAGE,
            ratingLabel = "—",
            reviewCountLabel = "",
            shopId = shopId.value.toString(),
        ),
        priceLabel = formatMarketplacePrice(priceAmount),
        description = description,
        reviews = null,
    )
}

fun List<ProductReview>.toProductReviewsMock(): ProductReviewsMock? {
    if (isEmpty()) return null
    return ProductReviewsMock(
        averageLabel = "",
        ratingsCountLabel = "",
        histogram = ProductReviewHistogram(0f, 0f, 0f, 0f, 0f),
        reviews = map { review ->
            ProductReviewItem(
                rating = review.rating.value,
                body = review.comment,
                authorName = "",
                authorInitial = "",
                avatarColorArgb = 0xFF888888,
                dateLabel = "",
                showAuthorMeta = false,
            )
        },
        showSummaryMetrics = false,
    )
}

fun ShopDetails.toStoreMock(): StoreMock =
    StoreMock(
        id = slug.value,
        name = title,
        coverUrl = PLACEHOLDER_IMAGE,
        avatarUrl = PLACEHOLDER_IMAGE,
        wordmarkUrl = null,
        brandColor = ShopPurple,
        useLightText = true,
        ratingLabel = "—",
        reviewCountLabel = "",
        navChips = emptyList(),
        collections = emptyList(),
        featuredProducts = emptyList(),
    )

fun ShopSummary.toCategoriesMerchantShop(): CategoriesMerchantShop {
    val displayName = title ?: slug.value
    val rating = plan?.title?.let { "پلن $it" } ?: ""
    return CategoriesMerchantShop(
        id = slug.value,
        name = displayName,
        ratingLabel = rating,
        logoUrl = PLACEHOLDER_IMAGE,
        imageUrls = List(4) { PLACEHOLDER_IMAGE },
    )
}
