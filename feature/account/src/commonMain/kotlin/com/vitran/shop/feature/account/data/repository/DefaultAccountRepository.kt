package com.vitran.shop.feature.account.data.repository

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.session.repository.SessionInvalidationListener
import com.vitran.shop.core.session.repository.SessionRoleCache
import com.vitran.shop.feature.account.data.mapper.toDomain
import com.vitran.shop.feature.account.data.remote.AccountApi
import com.vitran.shop.feature.account.data.remote.dto.UpdateProfileRequestDto
import com.vitran.shop.feature.account.domain.model.CurrentUserState
import com.vitran.shop.feature.account.domain.model.UpdateProfileCommand
import com.vitran.shop.feature.account.domain.model.User
import com.vitran.shop.feature.account.domain.repository.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class DefaultAccountRepository(
    private val accountApi: AccountApi,
    private val roleCache: SessionRoleCache,
    invalidationListeners: MutableList<SessionInvalidationListener>,
) : AccountRepository, SessionInvalidationListener {

    private val _currentUserState = MutableStateFlow<CurrentUserState>(CurrentUserState.Unknown)
    override val currentUserState: StateFlow<CurrentUserState> = _currentUserState.asStateFlow()

    init {
        invalidationListeners.add(this)
    }

    override suspend fun refreshCurrentUser(): AppResult<User> {
        _currentUserState.value = CurrentUserState.Loading
        return when (val result = accountApi.getCurrentUser()) {
            is AppResult.Success -> {
                val user = result.value.user.toDomain()
                applyUser(user)
                AppResult.Success(user)
            }
            is AppResult.Failure -> {
                _currentUserState.value = CurrentUserState.Error(result.error.message)
                AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun updateProfile(command: UpdateProfileCommand): AppResult<User> =
        when (val result = accountApi.updateProfile(
            UpdateProfileRequestDto(
                username = command.username,
                email = command.email,
            ),
        )) {
            is AppResult.Success -> {
                val user = result.value.user.toDomain()
                applyUser(user)
                AppResult.Success(user)
            }
            is AppResult.Failure -> AppResult.Failure(result.error)
        }

    override suspend fun clear() {
        _currentUserState.value = CurrentUserState.Unknown
        roleCache.clear()
    }

    override suspend fun onSessionInvalidated() {
        clear()
    }

    private fun applyUser(user: User) {
        _currentUserState.value = CurrentUserState.Available(user)
        roleCache.update(user.roles)
    }
}
