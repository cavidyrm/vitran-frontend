package com.vitran.shop.feature.admin.home.domain

import com.vitran.shop.core.domain.result.AppResult

interface AdminHomeRepository {
    suspend fun listSections(): AppResult<List<AdminHomeSection>>

    suspend fun getSection(id: Long): AppResult<AdminHomeSection>

    suspend fun createSection(command: SaveAdminHomeSectionCommand): AppResult<AdminHomeSection>

    suspend fun updateSection(id: Long, command: SaveAdminHomeSectionCommand): AppResult<AdminHomeSection>

    suspend fun deleteSection(id: Long): AppResult<Unit>

    suspend fun getBanner(id: Long): AppResult<AdminHomeBanner>

    suspend fun listBanners(sectionId: Long): AppResult<List<AdminHomeBanner>>

    suspend fun createBanner(sectionId: Long, command: SaveAdminHomeBannerCommand): AppResult<AdminHomeBanner>

    suspend fun updateBanner(id: Long, command: SaveAdminHomeBannerCommand): AppResult<AdminHomeBanner>

    suspend fun deleteBanner(id: Long): AppResult<Unit>
}
