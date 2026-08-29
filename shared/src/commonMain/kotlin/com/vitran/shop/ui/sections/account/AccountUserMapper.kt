package com.vitran.shop.ui.sections.account

import com.vitran.shop.core.domain.auth.UserRole
import com.vitran.shop.feature.account.domain.model.User

fun User.toAccountProfile(): AccountProfile {
    val roleNames = roles.map { role ->
        when (role) {
            UserRole.Customer -> "customer"
            UserRole.Seller -> "seller"
            UserRole.Admin -> "admin"
            UserRole.SuperAdmin -> "super_admin"
            is UserRole.Unknown -> role.rawValue
        }
    }
    val merchant = roles.any { it is UserRole.Seller || it is UserRole.Admin || it is UserRole.SuperAdmin }
    return AccountProfile(
        id = id.toString(),
        username = username.orEmpty(),
        firstName = "",
        lastName = "",
        email = email.orEmpty(),
        emailVerified = verified,
        phone = phone,
        roles = roleNames,
        hasStore = merchant,
        gender = AccountGender.Unspecified,
        birthday = "",
        shoeSize = null,
        topSize = null,
        bottomSize = null,
        skinType = null,
        skinUndertone = null,
        skinTone = null,
        hairType = null,
        hairColor = null,
        avatarUrl = null,
    )
}

fun accountProfileLoadingPlaceholder(): AccountProfile =
    AccountProfile(
        id = "",
        username = "",
        firstName = "",
        lastName = "",
        email = "",
        emailVerified = false,
        phone = "",
        roles = emptyList(),
        hasStore = false,
        gender = AccountGender.Unspecified,
        birthday = "",
        shoeSize = null,
        topSize = null,
        bottomSize = null,
        skinType = null,
        skinUndertone = null,
        skinTone = null,
        hairType = null,
        hairColor = null,
        avatarUrl = null,
    )
