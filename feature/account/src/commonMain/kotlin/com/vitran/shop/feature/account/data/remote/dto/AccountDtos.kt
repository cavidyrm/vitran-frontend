package com.vitran.shop.feature.account.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class UserCityDto(
    val id: Long,
    val slug: String,
    val name: String,
    val province: String? = null,
)

@Serializable
internal data class UserDto(
    val id: Long,
    val phone: String,
    val username: String? = null,
    val email: String? = null,
    @SerialName("full_name") val fullName: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    val sex: String? = null,
    @SerialName("city_id") val cityId: Long? = null,
    val city: UserCityDto? = null,
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
    @SerialName("full_name") val fullName: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    val sex: String? = null,
    @SerialName("city_id") val cityId: Long? = null,
    @SerialName("clear_city_id") val clearCityId: Boolean? = null,
    @SerialName("clear_sex") val clearSex: Boolean? = null,
    @SerialName("clear_avatar_url") val clearAvatarUrl: Boolean? = null,
)

@Serializable
internal data class UpdateProfileDataDto(
    val user: UserDto,
)

@Serializable
internal data class UsernameCheckDataDto(
    @SerialName("username_check") val usernameCheck: UsernameCheckDto,
)

@Serializable
internal data class UsernameCheckDto(
    val username: String,
    val available: Boolean,
)
