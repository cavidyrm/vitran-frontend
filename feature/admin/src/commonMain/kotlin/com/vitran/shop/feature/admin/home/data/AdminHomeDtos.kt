package com.vitran.shop.feature.admin.home.data

import com.vitran.shop.feature.admin.home.domain.AdminHomeBanner
import com.vitran.shop.feature.admin.home.domain.AdminHomeSection
import com.vitran.shop.feature.admin.home.domain.SaveAdminHomeBannerCommand
import com.vitran.shop.feature.admin.home.domain.SaveAdminHomeSectionCommand
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdminHomeSectionsDataDto(
    @SerialName("home_sections") val homeSections: List<AdminHomeSectionDto> = emptyList(),
)

@Serializable
internal data class AdminHomeSectionDataDto(
    @SerialName("home_section") val homeSection: AdminHomeSectionDto,
)

@Serializable
internal data class AdminHomeBannersDataDto(
    @SerialName("home_banners") val homeBanners: List<AdminHomeBannerDto> = emptyList(),
)

@Serializable
internal data class AdminHomeBannerDataDto(
    @SerialName("home_banner") val homeBanner: AdminHomeBannerDto,
)

@Serializable
internal data class AdminHomeSectionDto(
    val id: Long,
    val slug: String,
    val source: String,
    @SerialName("item_kind") val itemKind: String? = null,
    val layout: String,
    val title: String = "",
    @SerialName("item_limit") val itemLimit: Int = 12,
    @SerialName("sort_order") val sortOrder: Int = 0,
    val active: Boolean = true,
    val audience: String = "all",
    @SerialName("hide_when_empty") val hideWhenEmpty: Boolean = false,
    val platforms: List<String> = emptyList(),
    @SerialName("min_app_version") val minAppVersion: String? = null,
    @SerialName("starts_at") val startsAt: String? = null,
    @SerialName("ends_at") val endsAt: String? = null,
)

@Serializable
internal data class AdminHomeBannerDto(
    val id: Long,
    @SerialName("section_id") val sectionId: Long,
    val title: String? = null,
    @SerialName("image_url") val imageUrl: String,
    val action: AdminHomeBannerActionDto? = null,
    @SerialName("sort_order") val sortOrder: Int = 0,
    val active: Boolean = true,
    @SerialName("starts_at") val startsAt: String? = null,
    @SerialName("ends_at") val endsAt: String? = null,
)

@Serializable
internal data class AdminHomeBannerActionDto(
    val type: String? = null,
    val value: String? = null,
)

@Serializable
internal data class CreateAdminHomeSectionRequestDto(
    val slug: String,
    val source: String,
    val layout: String,
    val title: String,
    @SerialName("item_limit") val itemLimit: Int,
    @SerialName("sort_order") val sortOrder: Int,
    val active: Boolean,
    val audience: String,
    @SerialName("hide_when_empty") val hideWhenEmpty: Boolean,
    val platforms: List<String>,
    @SerialName("min_app_version") val minAppVersion: String,
    @SerialName("starts_at") val startsAt: String,
    @SerialName("ends_at") val endsAt: String,
)

@Serializable
internal data class UpdateAdminHomeSectionRequestDto(
    val source: String,
    val layout: String,
    val title: String,
    @SerialName("item_limit") val itemLimit: Int,
    @SerialName("sort_order") val sortOrder: Int,
    val active: Boolean,
    val audience: String,
    @SerialName("hide_when_empty") val hideWhenEmpty: Boolean,
    val platforms: List<String>,
    @SerialName("min_app_version") val minAppVersion: String,
    @SerialName("starts_at") val startsAt: String,
    @SerialName("ends_at") val endsAt: String,
)

@Serializable
internal data class SaveAdminHomeBannerRequestDto(
    @SerialName("image_url") val imageUrl: String,
    val title: String,
    @SerialName("action_type") val actionType: String,
    @SerialName("action_value") val actionValue: String,
    @SerialName("sort_order") val sortOrder: Int,
    val active: Boolean,
    @SerialName("starts_at") val startsAt: String,
    @SerialName("ends_at") val endsAt: String,
)

internal fun AdminHomeSectionDto.toDomain() = AdminHomeSection(
    id = id,
    slug = slug,
    source = source,
    itemKind = itemKind,
    layout = layout,
    title = title,
    itemLimit = itemLimit,
    sortOrder = sortOrder,
    active = active,
    audience = audience,
    hideWhenEmpty = hideWhenEmpty,
    platforms = platforms,
    minAppVersion = minAppVersion?.takeIf { it.isNotBlank() },
    startsAt = startsAt?.takeIf { it.isNotBlank() },
    endsAt = endsAt?.takeIf { it.isNotBlank() },
)

internal fun AdminHomeBannerDto.toDomain() = AdminHomeBanner(
    id = id,
    sectionId = sectionId,
    title = title?.takeIf { it.isNotBlank() },
    imageUrl = imageUrl,
    actionType = action?.type?.takeIf { it.isNotBlank() } ?: "none",
    actionValue = action?.value?.takeIf { it.isNotBlank() },
    sortOrder = sortOrder,
    active = active,
    startsAt = startsAt?.takeIf { it.isNotBlank() },
    endsAt = endsAt?.takeIf { it.isNotBlank() },
)

internal fun SaveAdminHomeSectionCommand.toCreateDto() = CreateAdminHomeSectionRequestDto(
    slug = slug.orEmpty(),
    source = source,
    layout = layout,
    title = title,
    itemLimit = itemLimit,
    sortOrder = sortOrder,
    active = active,
    audience = audience,
    hideWhenEmpty = hideWhenEmpty,
    platforms = platforms,
    minAppVersion = minAppVersion,
    startsAt = startsAt,
    endsAt = endsAt,
)

internal fun SaveAdminHomeSectionCommand.toUpdateDto() = UpdateAdminHomeSectionRequestDto(
    source = source,
    layout = layout,
    title = title,
    itemLimit = itemLimit,
    sortOrder = sortOrder,
    active = active,
    audience = audience,
    hideWhenEmpty = hideWhenEmpty,
    platforms = platforms,
    minAppVersion = minAppVersion,
    startsAt = startsAt,
    endsAt = endsAt,
)

internal fun SaveAdminHomeBannerCommand.toRequestDto() = SaveAdminHomeBannerRequestDto(
    imageUrl = imageUrl,
    title = title,
    actionType = actionType,
    actionValue = if (actionType == "none") "" else actionValue,
    sortOrder = sortOrder,
    active = active,
    startsAt = startsAt,
    endsAt = endsAt,
)
