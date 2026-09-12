package com.vitran.shop.feature.admin.users.domain.model

import com.vitran.shop.core.domain.auth.UserRole
import kotlinx.datetime.Instant

data class AdminUserCity(
    val id: Long,
    val slug: String,
    val name: String,
    val province: String? = null,
)

data class AdminUserSummary(
    val id: Long,
    val phone: String,
    val roles: Set<UserRole>,
    val verified: Boolean,
    val isActive: Boolean,
    val shopTypes: List<String> = emptyList(),
    val username: String? = null,
    val email: String? = null,
    val fullName: String? = null,
    val cityId: Long? = null,
)

data class AdminUserDetails(
    val id: Long,
    val phone: String,
    val roles: Set<UserRole>,
    val verified: Boolean,
    val isActive: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant,
    val shopTypes: List<String> = emptyList(),
    val username: String? = null,
    val email: String? = null,
    val fullName: String? = null,
    val avatarUrl: String? = null,
    val cityId: Long? = null,
    val city: AdminUserCity? = null,
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
    val roles: List<String>? = null,
)
