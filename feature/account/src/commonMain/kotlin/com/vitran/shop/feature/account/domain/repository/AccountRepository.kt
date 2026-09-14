package com.vitran.shop.feature.account.domain.repository

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.platform.file.SelectedFile
import com.vitran.shop.feature.account.domain.model.CurrentUserState
import com.vitran.shop.feature.account.domain.model.UpdateProfileCommand
import com.vitran.shop.feature.account.domain.model.User
import com.vitran.shop.feature.account.domain.model.UsernameAvailability
import kotlinx.coroutines.flow.StateFlow

interface AccountRepository {
    val currentUserState: StateFlow<CurrentUserState>

    fun observeCurrentUser(): StateFlow<CurrentUserState> = currentUserState

    suspend fun refreshCurrentUser(): AppResult<User>

    suspend fun checkUsernameAvailability(username: String): AppResult<UsernameAvailability>

    suspend fun uploadAvatar(image: SelectedFile): AppResult<User>

    suspend fun updateProfile(command: UpdateProfileCommand): AppResult<User>

    suspend fun clear()
}
