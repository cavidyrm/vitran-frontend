package com.vitran.shop.feature.admin.home.domain

data class AdminHomeSection(
    val id: Long,
    val slug: String,
    val source: String,
    val itemKind: String?,
    val layout: String,
    val title: String,
    val itemLimit: Int,
    val sortOrder: Int,
    val active: Boolean,
    val audience: String,
    val hideWhenEmpty: Boolean,
    val platforms: List<String>,
    val minAppVersion: String?,
    val startsAt: String?,
    val endsAt: String?,
)

data class AdminHomeBanner(
    val id: Long,
    val sectionId: Long,
    val title: String?,
    val imageUrl: String,
    val actionType: String,
    val actionValue: String?,
    val sortOrder: Int,
    val active: Boolean,
    val startsAt: String?,
    val endsAt: String?,
)

data class SaveAdminHomeSectionCommand(
    val slug: String? = null,
    val source: String,
    val layout: String,
    val title: String,
    val itemLimit: Int,
    val sortOrder: Int,
    val active: Boolean,
    val audience: String,
    val hideWhenEmpty: Boolean,
    val platforms: List<String>,
    val minAppVersion: String,
    val startsAt: String,
    val endsAt: String,
)

data class SaveAdminHomeBannerCommand(
    val imageUrl: String,
    val title: String,
    val actionType: String,
    val actionValue: String,
    val sortOrder: Int,
    val active: Boolean,
    val startsAt: String,
    val endsAt: String,
)
