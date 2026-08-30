package com.vitran.shop.feature.admin.users.data.repository

import com.vitran.shop.core.domain.pagination.PageResult
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.admin.users.data.mapper.toDomain
import com.vitran.shop.feature.admin.users.data.remote.AdminUserApi
import com.vitran.shop.feature.admin.users.data.remote.dto.UpdateAdminUserRequestDto
import com.vitran.shop.feature.admin.users.domain.model.AdminUserDetails
import com.vitran.shop.feature.admin.users.domain.model.AdminUserQuery
import com.vitran.shop.feature.admin.users.domain.model.AdminUserSummary
import com.vitran.shop.feature.admin.users.domain.model.UpdateAdminUserCommand
import com.vitran.shop.feature.admin.users.domain.repository.AdminUserRepository

internal class DefaultAdminUserRepository(
    private val api: AdminUserApi,
) : AdminUserRepository {
    override suspend fun getUsers(query: AdminUserQuery): AppResult<PageResult<AdminUserSummary>> =
        when (val result = api.getUsers(query)) {
            is AppResult.Success -> AppResult.Success(result.value.users.toDomain())
            is AppResult.Failure -> AppResult.Failure(result.error)
        }

    override suspend fun getUser(id: Long): AppResult<AdminUserDetails> =
        when (val result = api.getUser(id)) {
            is AppResult.Success -> AppResult.Success(result.value.user.toDomain())
            is AppResult.Failure -> AppResult.Failure(result.error)
        }

    override suspend fun updateUser(command: UpdateAdminUserCommand): AppResult<AdminUserDetails> =
        when (
            val result = api.updateUser(
                id = command.userId,
                request = UpdateAdminUserRequestDto(
                    isActive = command.isActive,
                    roles = command.roles,
                ),
            )
        ) {
            is AppResult.Success -> AppResult.Success(result.value.user.toDomain())
            is AppResult.Failure -> AppResult.Failure(result.error)
        }
}
