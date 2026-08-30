package com.vitran.shop.feature.admin.catalog.taxonomy.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class TaxonomyNameRequestDto(
    val name: String,
)
