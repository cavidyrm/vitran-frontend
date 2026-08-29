package com.vitran.shop.feature.taxonomy.data.mapper

import com.vitran.shop.feature.taxonomy.data.remote.dto.CategoryDetailsDto
import com.vitran.shop.feature.taxonomy.data.remote.dto.CategoryTreeNodeDto
import com.vitran.shop.feature.taxonomy.domain.model.CategoryDetails
import com.vitran.shop.feature.taxonomy.domain.model.CategoryNode
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug

internal fun CategoryTreeNodeDto.toDomain(): CategoryNode =
    CategoryNode(
        slug = CategorySlug(slug),
        sourceTitle = title,
        localizedName = name,
        isLeaf = isLeaf,
        children = children.orEmpty().map { it.toDomain() },
    )

internal fun CategoryDetailsDto.toDomain(): CategoryDetails =
    CategoryDetails(
        slug = CategorySlug(slug),
        sourceTitle = title,
        localizedName = name,
        fullName = fullName,
        isLeaf = isLeaf,
        iconUrl = iconUrl,
        children = children.orEmpty().map { it.toDomain() },
    )
