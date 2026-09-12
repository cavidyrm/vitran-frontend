package com.vitran.shop.feature.admin.users.data.mapper

import com.vitran.shop.core.domain.auth.UserRole
import com.vitran.shop.feature.admin.users.data.remote.dto.AdminUserCityDto
import com.vitran.shop.feature.admin.users.data.remote.dto.AdminUserDetailsDto
import com.vitran.shop.feature.admin.users.data.remote.dto.AdminUserListItemDto
import com.vitran.shop.feature.admin.users.domain.model.AdminUserCity
import com.vitran.shop.feature.admin.users.domain.model.AdminUserDetails
import com.vitran.shop.feature.admin.users.domain.model.AdminUserSummary
import kotlinx.datetime.Instant

internal fun AdminUserListItemDto.toDomain() = AdminUserSummary(
    id = id,
    phone = phone,
    roles = roles.map { UserRole.fromBackend(it) }.toSet(),
    verified = verified,
    isActive = isActive,
    shopTypes = shopTypes.orEmpty(),
    username = username,
    email = email,
    fullName = fullName,
    cityId = cityId,
)

internal fun AdminUserDetailsDto.toDomain() = AdminUserDetails(
    id = id,
    phone = phone,
    roles = roles.map { UserRole.fromBackend(it) }.toSet(),
    verified = verified,
    isActive = isActive,
    createdAt = Instant.parse(createdAt),
    updatedAt = Instant.parse(updatedAt),
    shopTypes = shopTypes.orEmpty(),
    username = username,
    email = email,
    fullName = fullName,
    avatarUrl = avatarUrl,
    cityId = cityId ?: city?.id,
    city = city?.toDomain(),
)

internal fun AdminUserCityDto.toDomain() = AdminUserCity(
    id = id,
    slug = slug,
    name = name,
    province = province,
)
