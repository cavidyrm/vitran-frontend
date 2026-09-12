package com.vitran.shop.feature.admin.users.presentation

import com.vitran.shop.core.domain.auth.UserRole
import com.vitran.shop.core.domain.pagination.PageResult
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.account.domain.model.CurrentUserState
import com.vitran.shop.feature.account.domain.model.UpdateProfileCommand
import com.vitran.shop.feature.account.domain.model.User
import com.vitran.shop.feature.account.domain.repository.AccountRepository
import com.vitran.shop.feature.admin.rbac.AdminPermissions
import com.vitran.shop.feature.admin.users.domain.model.AdminUserDetails
import com.vitran.shop.feature.admin.users.domain.model.AdminUserQuery
import com.vitran.shop.feature.admin.users.domain.model.AdminUserSummary
import com.vitran.shop.feature.admin.users.domain.model.UpdateAdminUserCommand
import com.vitran.shop.feature.admin.users.domain.repository.AdminUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class AdminUserDetailViewModelTest {
    @Test
    fun submit_preservesTargetSuperAdmin_andRefreshesCurrentUser() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val details = adminDetails()
            val repository = FakeAdminUserRepository(details)
            val accountRepository = FakeAccountRepository(currentUser())
            val viewModel = AdminUserDetailViewModel(
                userId = 42,
                repository = repository,
                accountRepository = accountRepository,
                permissions = AdminPermissions(),
            )
            advanceUntilIdle()

            viewModel.setRoleSelected(UserRole.User, false)
            viewModel.setRoleSelected(UserRole.Admin, true)
            viewModel.setActive(false)
            viewModel.submit()
            advanceUntilIdle()

            assertEquals(
                UpdateAdminUserCommand(
                    userId = 42,
                    isActive = false,
                    roles = listOf("admin", "super_admin"),
                ),
                repository.lastUpdate,
            )
            assertEquals(1, accountRepository.refreshCount)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun submit_omitsRolesWhenActorIsAdmin() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val details = adminDetails(
                id = 2,
                roles = setOf(UserRole.User, UserRole.Admin),
            )
            val repository = FakeAdminUserRepository(details)
            val viewModel = AdminUserDetailViewModel(
                userId = 2,
                repository = repository,
                accountRepository = FakeAccountRepository(
                    currentUser(id = 1, roles = setOf(UserRole.Admin)),
                ),
                permissions = AdminPermissions(),
            )
            advanceUntilIdle()

            viewModel.setRoleSelected(UserRole.User, false)
            viewModel.setRoleSelected(UserRole.Admin, true)
            viewModel.submit()
            advanceUntilIdle()

            assertEquals(
                UpdateAdminUserCommand(
                    userId = 2,
                    isActive = true,
                    roles = null,
                ),
                repository.lastUpdate,
            )
            assertEquals(
                setOf(UserRole.User, UserRole.Admin),
                viewModel.uiState.value.detail?.roles,
            )
        } finally {
            Dispatchers.resetMain()
        }
    }

    private fun adminDetails(
        id: Long = 42,
        roles: Set<UserRole> = setOf(UserRole.User, UserRole.SuperAdmin),
    ) = AdminUserDetails(
        id = id,
        phone = "09123456789",
        roles = roles,
        verified = true,
        isActive = true,
        createdAt = Instant.parse("2026-08-01T10:00:00Z"),
        updatedAt = Instant.parse("2026-08-20T10:00:00Z"),
    )

    private fun currentUser(
        id: Long = 42,
        roles: Set<UserRole> = setOf(UserRole.SuperAdmin),
    ) = User(
        id = id,
        phone = "09123456789",
        username = null,
        email = null,
        roles = roles,
        verified = true,
        isActive = true,
        createdAt = Instant.parse("2026-08-01T10:00:00Z"),
        updatedAt = Instant.parse("2026-08-20T10:00:00Z"),
    )
}

private class FakeAdminUserRepository(
    private val details: AdminUserDetails,
) : AdminUserRepository {
    var lastUpdate: UpdateAdminUserCommand? = null

    override suspend fun getUsers(query: AdminUserQuery) = AppResult.Success(
        PageResult<AdminUserSummary>(
            items = emptyList(),
            page = 1,
            perPage = 20,
            lastPage = 1,
            total = 0,
            hasMore = false,
        ),
    )

    override suspend fun getUser(id: Long) = AppResult.Success(details)

    override suspend fun updateUser(command: UpdateAdminUserCommand): AppResult<AdminUserDetails> {
        lastUpdate = command
        return AppResult.Success(
            details.copy(
                isActive = command.isActive,
                roles = command.roles?.map { UserRole.fromBackend(it) }?.toSet() ?: details.roles,
            ),
        )
    }
}

private class FakeAccountRepository(user: User) : AccountRepository {
    private val state = MutableStateFlow<CurrentUserState>(CurrentUserState.Available(user))
    override val currentUserState: StateFlow<CurrentUserState> = state
    var refreshCount = 0

    override suspend fun refreshCurrentUser(): AppResult<User> {
        refreshCount += 1
        return AppResult.Success((state.value as CurrentUserState.Available).user)
    }

    override suspend fun updateProfile(command: UpdateProfileCommand): AppResult<User> =
        AppResult.Success((state.value as CurrentUserState.Available).user)

    override suspend fun clear() {
        state.value = CurrentUserState.Unknown
    }
}
