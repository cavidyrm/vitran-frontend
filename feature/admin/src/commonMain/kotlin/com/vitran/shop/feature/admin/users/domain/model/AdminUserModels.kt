package com.vitran.shop.feature.admin.users.domain.model

import com.vitran.shop.core.domain.auth.UserRole
import kotlinx.datetime.Instant

data class AdminUserSummary(
    val id: Long,
    val phone: String,
    val roles: Set<UserRole>,
    val verified: Boolean,
    val isActive: Boolean,
)

data class AdminUserDetails(
    val id: Long,
    val phone: String,
    val roles: Set<UserRole>,
    val verified: Boolean,
    val isActive: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant,
)

data class AdminUserQuery(
    val role: String? = null,
    val phone: String? = null,
    val isActive: Boolean? = null,
    val page: Int = 1,
    val perPage: Int = 20,
)

data class UpdateAdminUserCommand(
    val userId: Long,
    val isActive: Boolean,
    val roles: List<String>,
)
