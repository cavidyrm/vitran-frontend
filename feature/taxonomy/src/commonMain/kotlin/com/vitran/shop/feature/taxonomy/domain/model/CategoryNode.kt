package com.vitran.shop.feature.taxonomy.domain.model

data class CategoryNode(
    val slug: CategorySlug,
    val sourceTitle: String,
    val localizedName: String?,
    val isLeaf: Boolean,
    val children: List<CategoryNode>,
) {
    val displayName: String
        get() = localizedName ?: sourceTitle
}
