package com.vitran.shop.feature.seller.product.domain.model

import com.vitran.shop.core.platform.file.SelectedFile
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug

/** `draft` | `active` | `inactive` on seller product create and update. */
enum class ProductListingStatus(val wireValue: String) {
    Draft("draft"),
    Active("active"),
    Inactive("inactive"),
}

/** Each product image must be at most 5 MB. Count still comes from the shop plan. */
const val MaxProductImageBytes: Long = 5L * 1024L * 1024L

data class CreateProductCommand(
    val shopId: ShopId,
    val title: String,
    val description: String,
    val priceAmount: Long,
    val category: CategorySlug,
    val desiredActive: Boolean,
    val images: List<SelectedFile> = emptyList(),
    val compareAtPriceAmount: Long? = null,
    val seoTitle: String? = null,
    val seoDescription: String? = null,
    val noindex: Boolean? = null,
    val slug: String? = null,
    val priceAfterOffAmount: Long? = null,
    val listingStatus: ProductListingStatus? = null,
    /** Attribute slug to value slug. Sent as `attributes[<slug>]`. */
    val attributes: Map<String, String> = emptyMap(),
)
