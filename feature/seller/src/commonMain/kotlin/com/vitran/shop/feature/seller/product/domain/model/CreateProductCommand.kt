package com.vitran.shop.feature.seller.product.domain.model

import com.vitran.shop.core.platform.file.SelectedFile
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug

data class CreateProductCommand(
    val shopId: ShopId,
    val title: String,
    val description: String,
    val priceAmount: Long,
    val category: CategorySlug,
    val desiredActive: Boolean,
    val images: List<SelectedFile> = emptyList(),
)
