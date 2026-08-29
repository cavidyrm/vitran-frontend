package com.vitran.shop.feature.home.data.mapper

import com.vitran.shop.feature.home.data.remote.dto.HomeSectionsDto
import com.vitran.shop.feature.home.domain.model.HomeFeed
import com.vitran.shop.feature.location.domain.model.CityId

internal fun HomeSectionsDto.toDomain(cityId: CityId?): HomeFeed =
    HomeFeed(
        cityId = cityId,
        featuredCount = featured.size,
        popularCount = popular.size,
        categoriesCount = categories.size,
        followingCount = following.size,
        personalCount = personal.size,
    )
