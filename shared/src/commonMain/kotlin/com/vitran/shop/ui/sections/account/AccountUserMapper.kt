package com.vitran.shop.ui.sections.account

import com.vitran.shop.feature.account.domain.model.User
import com.vitran.shop.feature.account.domain.model.splitFullName

fun User.toAccountProfile(): AccountProfile {
    val roleNames = roles.map { it.toBackend() }
    val (firstName, lastName) = splitFullName(fullName)
    return AccountProfile(
        id = id.toString(),
        username = username.orEmpty(),
        firstName = firstName,
        lastName = lastName,
        email = email.orEmpty(),
        emailVerified = verified,
        phone = phone,
        roles = roleNames,
        hasStore = shopTypes.isNotEmpty(),
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
        avatarUrl = avatarUrl,
        cityId = cityId,
        cityName = city?.name,
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
