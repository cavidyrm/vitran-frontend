package com.vitran.shop.feature.account.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.account.domain.model.CurrentUserState
import com.vitran.shop.feature.account.domain.model.UpdateProfileCommand
import com.vitran.shop.feature.account.domain.model.User
import com.vitran.shop.feature.account.domain.model.joinFullName
import com.vitran.shop.feature.account.domain.model.splitFullName
import com.vitran.shop.feature.account.domain.repository.AccountRepository
import com.vitran.shop.feature.location.domain.repository.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileCityOption(
    val id: Long,
    val name: String,
)

data class ProfileUiState(
    val isLoading: Boolean = true,
    val isUpdating: Boolean = false,
    val username: String = "",
    val email: String = "",
    val phone: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val avatarUrl: String = "",
    val cityId: Long? = null,
    val cityCleared: Boolean = false,
    val cities: List<ProfileCityOption> = emptyList(),
    val citiesError: String? = null,
    val showAvatarUrlField: Boolean = false,
    val error: String? = null,
)

sealed interface ProfileUiAction {
    data class UsernameChanged(val value: String) : ProfileUiAction
    data class EmailChanged(val value: String) : ProfileUiAction
    data class FirstNameChanged(val value: String) : ProfileUiAction
    data class LastNameChanged(val value: String) : ProfileUiAction
    data class AvatarUrlChanged(val value: String) : ProfileUiAction
    data class CitySelected(val cityId: Long?) : ProfileUiAction
    data object ToggleAvatarUrlField : ProfileUiAction
    data object Retry : ProfileUiAction
    data object Save : ProfileUiAction
}

class ProfileViewModel(
    private val accountRepository: AccountRepository,
    private val locationRepository: LocationRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            accountRepository.currentUserState.collect { state ->
                when (state) {
                    CurrentUserState.Unknown, CurrentUserState.Loading ->
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    is CurrentUserState.Available -> applyUser(state.user)
                    is CurrentUserState.Error -> _uiState.update {
                        it.copy(isLoading = false, error = state.message ?: "بارگذاری ناموفق")
                    }
                }
            }
        }
        refresh()
        loadCities()
    }

    fun onAction(action: ProfileUiAction) {
        when (action) {
            is ProfileUiAction.UsernameChanged -> _uiState.update { it.copy(username = action.value) }
            is ProfileUiAction.EmailChanged -> _uiState.update { it.copy(email = action.value) }
            is ProfileUiAction.FirstNameChanged -> _uiState.update { it.copy(firstName = action.value) }
            is ProfileUiAction.LastNameChanged -> _uiState.update { it.copy(lastName = action.value) }
            is ProfileUiAction.AvatarUrlChanged -> _uiState.update { it.copy(avatarUrl = action.value) }
            is ProfileUiAction.CitySelected -> _uiState.update {
                it.copy(cityId = action.cityId, cityCleared = action.cityId == null)
            }
            ProfileUiAction.ToggleAvatarUrlField -> _uiState.update {
                it.copy(showAvatarUrlField = !it.showAvatarUrlField)
            }
            ProfileUiAction.Retry -> {
                refresh()
                loadCities()
            }
            ProfileUiAction.Save -> save()
        }
    }

    private fun applyUser(user: User) {
        val (firstName, lastName) = splitFullName(user.fullName)
        _uiState.update {
            it.copy(
                isLoading = false,
                username = user.username.orEmpty(),
                email = user.email.orEmpty(),
                phone = user.phone,
                firstName = firstName,
                lastName = lastName,
                avatarUrl = user.avatarUrl.orEmpty(),
                cityId = user.cityId,
                cityCleared = false,
                error = null,
            )
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            accountRepository.refreshCurrentUser()
        }
    }

    private fun loadCities() {
        viewModelScope.launch {
            when (val result = locationRepository.getCities()) {
                is AppResult.Success -> _uiState.update {
                    it.copy(
                        cities = result.value.map { city ->
                            ProfileCityOption(id = city.id.value, name = city.name)
                        },
                        citiesError = null,
                    )
                }
                is AppResult.Failure -> _uiState.update {
                    it.copy(citiesError = result.error.message)
                }
            }
        }
    }

    private fun save() {
        val state = _uiState.value
        if (state.isUpdating) return
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, error = null) }
            when (
                val result = accountRepository.updateProfile(
                    UpdateProfileCommand(
                        username = state.username.ifBlank { null },
                        email = state.email.ifBlank { null },
                        fullName = joinFullName(state.firstName, state.lastName),
                        avatarUrl = state.avatarUrl.ifBlank { null },
                        cityId = state.cityId,
                        clearCityId = true.takeIf { state.cityCleared },
                    ),
                )
            ) {
                is AppResult.Success -> _uiState.update {
                    it.copy(isUpdating = false, showAvatarUrlField = false)
                }
                is AppResult.Failure -> _uiState.update {
                    it.copy(isUpdating = false, error = result.error.message)
                }
            }
        }
    }
}
