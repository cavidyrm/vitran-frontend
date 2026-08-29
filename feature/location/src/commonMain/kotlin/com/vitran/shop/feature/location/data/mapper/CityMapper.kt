package com.vitran.shop.feature.location.data.mapper

import com.vitran.shop.feature.location.data.remote.dto.CityDto
import com.vitran.shop.feature.location.domain.model.City
import com.vitran.shop.feature.location.domain.model.CityId
import com.vitran.shop.feature.location.domain.model.CitySlug

internal fun CityDto.toDomain(): City =
    City(
        id = CityId(id),
        slug = CitySlug(slug),
        name = name,
    )
