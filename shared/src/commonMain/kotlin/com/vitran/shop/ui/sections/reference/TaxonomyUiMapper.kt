package com.vitran.shop.ui.sections.reference

import com.vitran.shop.feature.taxonomy.domain.model.CategoryNode
import com.vitran.shop.ui.components.admin.AdminTaxonomyNode

fun CategoryNode.toAdminTaxonomyNode(): AdminTaxonomyNode =
    AdminTaxonomyNode(
        id = slug.value,
        label = displayName,
        children = children.map { it.toAdminTaxonomyNode() },
    )

fun List<CategoryNode>.toAdminTaxonomyNodes(): List<AdminTaxonomyNode> =
    map { it.toAdminTaxonomyNode() }
