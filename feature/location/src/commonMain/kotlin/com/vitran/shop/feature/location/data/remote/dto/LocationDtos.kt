package com.vitran.shop.feature.location.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class CityDto(
    val id: Long,
    val slug: String,
    val name: String,
)

@Serializable
internal data class CitiesDataDto(
    val cities: List<CityDto>,
)

@Serializable
internal data class CityDataDto(
    val city: CityDto,
)
