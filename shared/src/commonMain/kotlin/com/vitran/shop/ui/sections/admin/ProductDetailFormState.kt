package com.vitran.shop.ui.sections.admin

import com.vitran.shop.feature.admin.moderation.domain.AdminProductDetails
import com.vitran.shop.feature.admin.moderation.domain.AdminProductImage
import com.vitran.shop.feature.seller.product.domain.model.SellerProductDetails
import com.vitran.shop.feature.seller.product.domain.model.SellerProductImage

/**
 * Read-only form snapshot for a stored product. Local-only create fields stay empty.
 */
internal fun AdminProductDetails.toProductDetailFormState(storeName: String = ""): CreateProductFormState =
    productDetailFormState(
        title = title,
        description = description,
        categorySlug = categorySlug,
        priceAmount = priceAmount,
        compareAtPriceAmount = compareAtPriceAmount,
        active = active,
        images = images.toAdminMedia(),
        slug = slug,
        seoTitle = seoTitle,
        seoDescription = seoDescription,
        noindex = noindex,
        storeName = storeName,
    )

internal fun SellerProductDetails.toProductDetailFormState(storeName: String = ""): CreateProductFormState =
    productDetailFormState(
        title = title,
        description = description,
        categorySlug = categorySlug?.value,
        priceAmount = priceAmount,
        compareAtPriceAmount = compareAtPriceAmount,
        active = active,
        images = images.toSellerMedia(),
        slug = slug,
        seoTitle = seoTitle,
        seoDescription = seoDescription,
        noindex = noindex,
        storeName = storeName,
    )

private fun productDetailFormState(
    title: String,
    description: String?,
    categorySlug: String?,
    priceAmount: Long?,
    compareAtPriceAmount: Long?,
    active: Boolean,
    images: List<ProductMediaItem>,
    slug: String?,
    seoTitle: String?,
    seoDescription: String?,
    noindex: Boolean,
    storeName: String,
): CreateProductFormState {
    val slugText = slug?.trim().orEmpty()
    val seoTitleText = seoTitle?.trim().orEmpty()
    val seoDescriptionText = seoDescription?.trim().orEmpty()
    val compareAt = compareAtPriceAmount?.takeIf { it > 0L }
    return CreateProductFormState(
        title = title,
        description = description.orEmpty(),
        media = images,
        categoryId = categorySlug?.trim()?.takeIf { it.isNotEmpty() },
        price = priceAmount?.let { formatMoneyInput(it.toString()) }.orEmpty(),
        compareAtEnabled = compareAt != null,
        compareAtPrice = compareAt?.let { formatMoneyInput(it.toString()) }.orEmpty(),
        status = if (active) ProductPublishStatus.Active else ProductPublishStatus.Draft,
        slug = slugText,
        slugManuallyEdited = slugText.isNotBlank(),
        seoTitle = seoTitleText,
        seoDescription = seoDescriptionText,
        noindex = noindex,
        seoExpanded = seoTitleText.isNotBlank() || seoDescriptionText.isNotBlank(),
        storeName = storeName,
    )
}

private fun List<AdminProductImage>.toAdminMedia(): List<ProductMediaItem> =
    sortedWith(compareBy<AdminProductImage> { !it.isMain }.thenBy { it.sortOrder })
        .map { image ->
            ProductMediaItem(
                id = image.id.toString(),
                url = image.url,
            )
        }

private fun List<SellerProductImage>.toSellerMedia(): List<ProductMediaItem> =
    sortedWith(compareBy<SellerProductImage> { !it.isMain }.thenBy { it.sortOrder })
        .map { image ->
            ProductMediaItem(
                id = image.id.value.toString(),
                url = image.url,
            )
        }
