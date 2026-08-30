package com.vitran.shop.feature.admin.users.domain.repository

import com.vitran.shop.core.domain.pagination.PageResult
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.admin.users.domain.model.AdminUserDetails
import com.vitran.shop.feature.admin.users.domain.model.AdminUserQuery
import com.vitran.shop.feature.admin.users.domain.model.AdminUserSummary
import com.vitran.shop.feature.admin.users.domain.model.UpdateAdminUserCommand

interface AdminUserRepository {
    suspend fun getUsers(query: AdminUserQuery): AppResult<PageResult<AdminUserSummary>>

    suspend fun getUser(id: Long): AppResult<AdminUserDetails>

    suspend fun updateUser(command: UpdateAdminUserCommand): AppResult<AdminUserDetails>
}
