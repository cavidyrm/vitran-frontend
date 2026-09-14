package com.vitran.shop.feature.account.domain.model

import com.vitran.shop.core.domain.auth.UserRole
import kotlinx.datetime.Instant

data class UserCity(
    val id: Long,
    val slug: String,
    val name: String,
    val province: String? = null,
)

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
    val fullName: String? = null,
    val avatarUrl: String? = null,
    val sex: PersonSex? = null,
    val cityId: Long? = null,
    val city: UserCity? = null,
    val referralCode: String? = null,
    val wishlistShareSlug: String? = null,
    val wishlistPublic: Boolean = false,
    val productMatchNotify: Boolean = true,
    val shopTypes: List<String> = emptyList(),
)

sealed interface CurrentUserState {
    data object Unknown : CurrentUserState
    data object Loading : CurrentUserState
    data class Available(val user: User) : CurrentUserState
    data class Error(val message: String?) : CurrentUserState
}

data class UpdateProfileCommand(
    val username: String? = null,
    val email: String? = null,
    val fullName: String? = null,
    val avatarUrl: String? = null,
    val sex: PersonSex? = null,
    val cityId: Long? = null,
    val clearCityId: Boolean? = null,
    val clearSex: Boolean? = null,
    val clearAvatarUrl: Boolean? = null,
)

fun splitFullName(fullName: String?): Pair<String, String> {
    val trimmed = fullName?.trim().orEmpty()
    if (trimmed.isEmpty()) return "" to ""
    val space = trimmed.indexOf(' ')
    return if (space < 0) {
        trimmed to ""
    } else {
        trimmed.substring(0, space) to trimmed.substring(space + 1).trim()
    }
}

fun joinFullName(firstName: String, lastName: String): String? =
    listOf(firstName, lastName)
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .joinToString(" ")
        .ifBlank { null }
