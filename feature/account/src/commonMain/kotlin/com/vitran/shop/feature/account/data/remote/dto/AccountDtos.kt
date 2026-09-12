package com.vitran.shop.feature.account.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class UserDto(
    val id: Long,
    val phone: String,
    val username: String? = null,
    val email: String? = null,
    @SerialName("full_name") val fullName: String? = null,
    @SerialName("referral_code") val referralCode: String? = null,
    @SerialName("wishlist_share_slug") val wishlistShareSlug: String? = null,
    @SerialName("wishlist_public") val wishlistPublic: Boolean = false,
    @SerialName("product_match_notify") val productMatchNotify: Boolean = true,
    val roles: List<String> = emptyList(),
    @SerialName("shop_types") val shopTypes: List<String>? = null,
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
