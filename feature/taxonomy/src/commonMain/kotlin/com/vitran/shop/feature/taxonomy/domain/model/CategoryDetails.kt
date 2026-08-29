package com.vitran.shop.feature.taxonomy.domain.model

data class CategoryDetails(
    val slug: CategorySlug,
    val sourceTitle: String,
    val localizedName: String?,
    val fullName: String?,
    val isLeaf: Boolean,
    val iconUrl: String?,
    val children: List<CategoryNode>,
) {
    val displayName: String
        get() = localizedName ?: sourceTitle
}
