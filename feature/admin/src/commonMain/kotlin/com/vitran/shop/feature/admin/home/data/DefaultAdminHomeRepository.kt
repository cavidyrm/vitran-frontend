package com.vitran.shop.feature.admin.home.data

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.admin.home.domain.AdminHomeRepository
import com.vitran.shop.feature.admin.home.domain.SaveAdminHomeBannerCommand
import com.vitran.shop.feature.admin.home.domain.SaveAdminHomeSectionCommand

internal class DefaultAdminHomeRepository(
    private val api: AdminHomeApi,
) : AdminHomeRepository {
    override suspend fun listSections() =
        api.listSections().mapSuccess { data -> data.homeSections.map { it.toDomain() } }

    override suspend fun getSection(id: Long) =
        api.getSection(id).mapSuccess { it.homeSection.toDomain() }

    override suspend fun createSection(command: SaveAdminHomeSectionCommand) =
        api.createSection(command).mapSuccess { it.homeSection.toDomain() }

    override suspend fun updateSection(id: Long, command: SaveAdminHomeSectionCommand) =
        api.updateSection(id, command).mapSuccess { it.homeSection.toDomain() }

    override suspend fun deleteSection(id: Long) = api.deleteSection(id)

    override suspend fun getBanner(id: Long) =
        api.getBanner(id).mapSuccess { it.homeBanner.toDomain() }

    override suspend fun listBanners(sectionId: Long) =
        api.listBanners(sectionId).mapSuccess { data -> data.homeBanners.map { it.toDomain() } }

    override suspend fun createBanner(sectionId: Long, command: SaveAdminHomeBannerCommand) =
        api.createBanner(sectionId, command).mapSuccess { it.homeBanner.toDomain() }

    override suspend fun updateBanner(id: Long, command: SaveAdminHomeBannerCommand) =
        api.updateBanner(id, command).mapSuccess { it.homeBanner.toDomain() }

    override suspend fun deleteBanner(id: Long) = api.deleteBanner(id)
}

private inline fun <T, R> AppResult<T>.mapSuccess(transform: (T) -> R): AppResult<R> = when (this) {
    is AppResult.Success -> AppResult.Success(transform(value))
    is AppResult.Failure -> this
}
