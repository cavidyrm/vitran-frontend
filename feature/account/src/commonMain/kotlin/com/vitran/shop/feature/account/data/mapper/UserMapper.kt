package com.vitran.shop.feature.account.data.mapper

import com.vitran.shop.core.domain.auth.UserRole
import com.vitran.shop.feature.account.data.remote.dto.UserCityDto
import com.vitran.shop.feature.account.data.remote.dto.UserDto
import com.vitran.shop.feature.account.domain.model.User
import com.vitran.shop.feature.account.domain.model.UserCity
import kotlinx.datetime.Instant

internal fun UserDto.toDomain(): User = User(
    id = id,
    phone = phone,
    username = username,
    email = email,
    roles = roles.map { UserRole.fromBackend(it) }.toSet(),
    verified = verified,
    isActive = isActive,
    createdAt = Instant.parse(createdAt),
    updatedAt = Instant.parse(updatedAt),
    fullName = fullName,
    avatarUrl = avatarUrl,
    sex = sex.toPersonSex(),
    cityId = cityId ?: city?.id,
    city = city?.toDomain(),
    referralCode = referralCode,
    wishlistShareSlug = wishlistShareSlug,
    wishlistPublic = wishlistPublic,
    productMatchNotify = productMatchNotify,
    shopTypes = shopTypes.orEmpty(),
)

internal fun UserCityDto.toDomain(): UserCity = UserCity(
    id = id,
    slug = slug,
    name = name,
    province = province,
)
