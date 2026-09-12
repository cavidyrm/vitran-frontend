package com.vitran.shop.ui.sections.account.users

import com.vitran.shop.core.domain.auth.UserRole
import com.vitran.shop.feature.account.domain.model.splitFullName
import com.vitran.shop.feature.admin.users.domain.model.AdminUserDetails
import com.vitran.shop.feature.admin.users.domain.model.AdminUserSummary

fun AdminUserSummary.toAccountUser(): AccountUser {
    val (firstName, lastName) = displayNameParts(fullName, phone)
    return AccountUser(
        id = id.toInt(),
        firstName = firstName,
        lastName = lastName,
        phone = phone,
        roles = roles.toAccountUserRoles(),
        status = if (isActive) AccountUserStatus.Active else AccountUserStatus.Inactive,
        joinedJalali = "",
        email = email.orEmpty(),
        phoneVerified = verified,
    )
}

fun AdminUserDetails.toAccountUser(): AccountUser {
    val (firstName, lastName) = displayNameParts(fullName, phone)
    return AccountUser(
        id = id.toInt(),
        firstName = firstName,
        lastName = lastName,
        phone = phone,
        roles = roles.toAccountUserRoles(),
        status = if (isActive) AccountUserStatus.Active else AccountUserStatus.Inactive,
        joinedJalali = createdAt.toString(),
        email = email.orEmpty(),
        phoneVerified = verified,
    )
}

fun UserRole.toAccountUserRole(): AccountUserRole? =
    when (this) {
        UserRole.User -> AccountUserRole.User
        UserRole.Admin -> AccountUserRole.Admin
        UserRole.SuperAdmin -> AccountUserRole.SuperAdmin
        is UserRole.Unknown -> null
    }

fun AccountUserRole.toUserRole(): UserRole =
    when (this) {
        AccountUserRole.User -> UserRole.User
        AccountUserRole.Admin -> UserRole.Admin
        AccountUserRole.SuperAdmin -> UserRole.SuperAdmin
    }

fun displayedAccountUserRoles(
    selectedEditableRoles: Set<UserRole>,
    existingRoles: Set<UserRole>,
    assignableRoles: Collection<UserRole>,
): List<AccountUserRole> {
    val assignable = assignableRoles.toSet()
    return buildList {
        addAll(selectedEditableRoles)
        existingRoles.forEach { role ->
            if (role !in assignable) add(role)
        }
    }.mapNotNull(UserRole::toAccountUserRole).distinct()
}

private fun Set<UserRole>.toAccountUserRoles(): List<AccountUserRole> =
    mapNotNull(UserRole::toAccountUserRole).distinct()

private fun displayNameParts(fullName: String?, phone: String): Pair<String, String> {
    val (first, last) = splitFullName(fullName)
    return if (first.isBlank()) phone to "" else first to last
}
