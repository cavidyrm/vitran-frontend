package com.vitran.shop.feature.admin.users.data.remote.dto

import com.vitran.shop.core.network.pagination.PageDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdminUserListItemDto(
    val id: Long,
    val phone: String,
    val roles: List<String> = emptyList(),
    val verified: Boolean = false,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
)

@Serializable
internal data class AdminUserDetailsDto(
    val id: Long,
    val phone: String,
    val roles: List<String> = emptyList(),
    val verified: Boolean = false,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
)

@Serializable
internal data class AdminUsersDataDto(
    val users: PageDto<AdminUserListItemDto>,
)

@Serializable
internal data class AdminUserDataDto(
    val user: AdminUserDetailsDto,
)

@Serializable
internal data class UpdateAdminUserRequestDto(
    @SerialName("is_active") val isActive: Boolean,
    val roles: List<String>,
)
