package com.vitran.shop.feature.account.domain.model

import com.vitran.shop.core.domain.auth.UserRole
import kotlinx.datetime.Instant

data class User(
    val id: Long,
    val phone: String,
    val username: String?,
    val email: String?,
    val roles: Set<UserRole>,
    val verified: Boolean,
    val isActive: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant,
)

sealed interface CurrentUserState {
    data object Unknown : CurrentUserState
    data object Loading : CurrentUserState
    data class Available(val user: User) : CurrentUserState
    data class Error(val message: String?) : CurrentUserState
}

data class UpdateProfileCommand(
    val username: String?,
    val email: String?,
)
