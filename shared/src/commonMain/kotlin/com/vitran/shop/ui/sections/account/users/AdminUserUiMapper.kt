package com.vitran.shop.ui.sections.account.users

import com.vitran.shop.core.domain.auth.UserRole
import com.vitran.shop.feature.admin.users.domain.model.AdminUserDetails
import com.vitran.shop.feature.admin.users.domain.model.AdminUserSummary

fun AdminUserSummary.toAccountUser(): AccountUser =
    AccountUser(
        id = id.toInt(),
        firstName = phone,
        lastName = "",
        phone = phone,
        roles = roles.toAccountUserRoles(),
        status = if (isActive) AccountUserStatus.Active else AccountUserStatus.Inactive,
        joinedJalali = "",
        email = "",
        phoneVerified = verified,
    )

fun AdminUserDetails.toAccountUser(): AccountUser =
    AccountUser(
        id = id.toInt(),
        firstName = phone,
        lastName = "",
        phone = phone,
        roles = roles.toAccountUserRoles(),
        status = if (isActive) AccountUserStatus.Active else AccountUserStatus.Inactive,
        joinedJalali = createdAt.toString(),
        email = "",
        phoneVerified = verified,
    )

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
