package com.vitran.shop.feature.admin.catalog.location.data.remote.dto

import com.vitran.shop.feature.admin.catalog.location.domain.CreateCityCommand
import com.vitran.shop.feature.admin.catalog.location.domain.UpdateCityCommand
import com.vitran.shop.feature.location.domain.model.City
import com.vitran.shop.feature.location.domain.model.CityId
import com.vitran.shop.feature.location.domain.model.CitySlug
import kotlinx.serialization.Serializable

@Serializable
internal data class CityMutationRequestDto(
    val slug: String,
    val name: String,
)

@Serializable
internal data class AdminCityDto(
    val id: Long,
    val slug: String,
    val name: String,
)

@Serializable
internal data class AdminCityDataDto(
    val city: AdminCityDto,
)

internal fun CreateCityCommand.toRequestDto() =
    CityMutationRequestDto(slug = slug, name = name)

internal fun UpdateCityCommand.toRequestDto() =
    CityMutationRequestDto(slug = slug, name = name)

internal fun AdminCityDto.toDomain() =
    City(id = CityId(id), slug = CitySlug(slug), name = name)
