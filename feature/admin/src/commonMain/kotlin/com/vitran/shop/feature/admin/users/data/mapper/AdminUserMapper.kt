package com.vitran.shop.feature.admin.users.data.mapper

import com.vitran.shop.core.domain.auth.UserRole
import com.vitran.shop.core.domain.pagination.PageResult
import com.vitran.shop.core.network.pagination.PageDto
import com.vitran.shop.feature.admin.users.data.remote.dto.AdminUserDetailsDto
import com.vitran.shop.feature.admin.users.data.remote.dto.AdminUserListItemDto
import com.vitran.shop.feature.admin.users.domain.model.AdminUserDetails
import com.vitran.shop.feature.admin.users.domain.model.AdminUserSummary
import kotlinx.datetime.Instant

internal fun AdminUserListItemDto.toDomain() = AdminUserSummary(
    id = id,
    phone = phone,
    roles = roles.map { UserRole.fromBackend(it) }.toSet(),
    verified = verified,
    isActive = isActive,
)

internal fun AdminUserDetailsDto.toDomain() = AdminUserDetails(
    id = id,
    phone = phone,
    roles = roles.map { UserRole.fromBackend(it) }.toSet(),
    verified = verified,
    isActive = isActive,
    createdAt = Instant.parse(createdAt),
    updatedAt = Instant.parse(updatedAt),
)

internal fun PageDto<AdminUserListItemDto>.toDomain() = PageResult(
    items = results.map { it.toDomain() },
    page = page,
    perPage = perPage,
    lastPage = lastPage,
    total = total,
    hasMore = hasMore,
)
