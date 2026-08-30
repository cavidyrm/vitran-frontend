package com.vitran.shop.feature.seller.product.domain.model

import com.vitran.shop.core.platform.file.SelectedFile
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug

/**
 * Multipart PATCH fields. Null means omit from the request.
 * New [images] are appended by the backend (total ≤ 5).
 */
data class UpdateProductCommand(
    val productId: ProductId,
    val title: String? = null,
    val description: String? = null,
    val priceAmount: Long? = null,
    val category: CategorySlug? = null,
    val desiredActive: Boolean? = null,
    val images: List<SelectedFile> = emptyList(),
)
