package com.vitran.shop.feature.taxonomy.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class CategoryTreeNodeDto(
    val slug: String,
    val title: String,
    val name: String? = null,
    @SerialName("is_leaf")
    val isLeaf: Boolean,
    val children: List<CategoryTreeNodeDto>? = null,
)

@Serializable
internal data class CategoryDetailsDto(
    val slug: String,
    val title: String,
    val name: String? = null,
    @SerialName("full_name")
    val fullName: String? = null,
    @SerialName("is_leaf")
    val isLeaf: Boolean,
    @SerialName("icon_url")
    val iconUrl: String? = null,
    val children: List<CategoryTreeNodeDto>? = null,
)

@Serializable
internal data class CategoriesDataDto(
    val categories: List<CategoryTreeNodeDto>,
)

@Serializable
internal data class CategoryDataDto(
    val category: CategoryDetailsDto,
)
