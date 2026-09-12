package com.vitran.shop.feature.home.data.mapper

import com.vitran.shop.feature.home.data.remote.dto.HomeScreenDataDto
import com.vitran.shop.feature.home.data.remote.dto.HomeScreenSectionDto
import com.vitran.shop.feature.home.data.remote.dto.HomeSectionPageDto
import com.vitran.shop.feature.home.data.remote.dto.HomeSectionProductDto
import com.vitran.shop.feature.home.data.remote.dto.HomeSectionsDto
import com.vitran.shop.feature.home.domain.model.HomeFeed
import com.vitran.shop.feature.home.domain.model.HomeScreen
import com.vitran.shop.feature.home.domain.model.HomeScreenSection
import com.vitran.shop.feature.home.domain.model.HomeSectionPage
import com.vitran.shop.feature.home.domain.model.HomeSectionProduct
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

internal fun HomeScreenDataDto.toDomain(cityId: CityId?): HomeScreen =
    HomeScreen(
        cityId = cityId,
        sections = screen?.sections.orEmpty().map(HomeScreenSectionDto::toDomain),
    )

internal fun HomeScreenSectionDto.toDomain(): HomeScreenSection =
    HomeScreenSection(
        slug = slug,
        title = title,
        type = type,
        layout = layout,
        source = source,
        morePath = more?.path,
    )

internal fun HomeSectionPageDto.toDomain(): HomeSectionPage =
    HomeSectionPage(
        slug = slug,
        title = title,
        type = type,
        layout = layout,
        source = source,
        perPage = perPage,
        hasMore = hasMore,
        nextCursor = nextCursor,
        products = results.map(HomeSectionProductDto::toDomain),
    )

internal fun HomeSectionProductDto.toDomain(): HomeSectionProduct =
    HomeSectionProduct(
        id = id,
        title = title,
        priceAmount = price,
        compareAtPriceAmount = compareAtPrice,
    )
