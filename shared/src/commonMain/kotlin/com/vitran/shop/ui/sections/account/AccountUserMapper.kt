package com.vitran.shop.ui.sections.account

import com.vitran.shop.feature.account.domain.model.User

fun User.toAccountProfile(): AccountProfile {
    val roleNames = roles.map { it.toBackend() }
    return AccountProfile(
        id = id.toString(),
        username = username.orEmpty(),
        firstName = "",
        lastName = "",
        email = email.orEmpty(),
        emailVerified = verified,
        phone = phone,
        roles = roleNames,
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
