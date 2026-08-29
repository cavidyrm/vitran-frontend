package com.vitran.shop.feature.account.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class UserDto(
    val id: Long,
    val phone: String,
    val username: String? = null,
    val email: String? = null,
    val roles: List<String> = emptyList(),
    val verified: Boolean = false,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
)

@Serializable
internal data class GetCurrentUserDataDto(
    val user: UserDto,
)

@Serializable
internal data class UpdateProfileRequestDto(
    val username: String? = null,
    val email: String? = null,
)

@Serializable
internal data class UpdateProfileDataDto(
    val user: UserDto,
)
