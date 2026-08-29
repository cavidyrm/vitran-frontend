package com.vitran.shop.ui.sections.reference

import com.vitran.shop.feature.taxonomy.domain.model.CategoryNode
import com.vitran.shop.ui.sections.categories.BrowseCategory
import com.vitran.shop.ui.sections.categories.browseCategoryVisualsAt

fun CategoryNode.toBrowseCategory(index: Int): BrowseCategory {
    val visuals = browseCategoryVisualsAt(index)
    return BrowseCategory(
        id = slug.value,
        title = displayName,
        backgroundColor = visuals.backgroundColor,
        imageUrl1 = visuals.imageUrl1,
        imageUrl2 = visuals.imageUrl2,
    )
}

fun List<CategoryNode>.toBrowseCategories(): List<BrowseCategory> =
    mapIndexed { index, node -> node.toBrowseCategory(index) }
