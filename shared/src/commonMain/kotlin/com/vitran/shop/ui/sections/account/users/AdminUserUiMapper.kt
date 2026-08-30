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
        UserRole.Customer -> AccountUserRole.Customer
        UserRole.Seller -> AccountUserRole.Seller
        UserRole.Admin, UserRole.SuperAdmin -> AccountUserRole.Manager
        is UserRole.Unknown -> null
    }

fun AccountUserRole.toUserRole(): UserRole? =
    when (this) {
        AccountUserRole.Customer -> UserRole.Customer
        AccountUserRole.Seller -> UserRole.Seller
        AccountUserRole.Manager -> UserRole.Admin
        AccountUserRole.Support -> null
    }

private fun Set<UserRole>.toAccountUserRoles(): List<AccountUserRole> =
    mapNotNull(UserRole::toAccountUserRole).distinct()
