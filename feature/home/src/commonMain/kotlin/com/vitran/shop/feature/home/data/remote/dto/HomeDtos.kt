package com.vitran.shop.feature.home.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Home envelope — section item schemas unverified (Postman empty arrays; live vitran.ir probe returned 404).
 * Arrays deserialize as [JsonElement] until non-empty contract is captured.
 */
@Serializable
data class HomeDataDto(
    val home: HomeSectionsDto,
)

@Serializable
data class HomeSectionsDto(
    val featured: List<JsonElement> = emptyList(),
    val popular: List<JsonElement> = emptyList(),
    val categories: List<JsonElement> = emptyList(),
    val following: List<JsonElement> = emptyList(),
    val personal: List<JsonElement> = emptyList(),
)
