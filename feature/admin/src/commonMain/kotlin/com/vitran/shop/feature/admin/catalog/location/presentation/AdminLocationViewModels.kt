package com.vitran.shop.feature.admin.catalog.location.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.error.splitForForm
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.account.domain.model.CurrentUserState
import com.vitran.shop.feature.account.domain.repository.AccountRepository
import com.vitran.shop.feature.admin.catalog.location.domain.AdminLocationError
import com.vitran.shop.feature.admin.catalog.location.domain.AdminLocationRepository
import com.vitran.shop.feature.admin.catalog.location.domain.CreateCityCommand
import com.vitran.shop.feature.admin.catalog.location.domain.UpdateCityCommand
import com.vitran.shop.feature.admin.rbac.AdminPermissions
import com.vitran.shop.feature.admin.state.AdminSessionStateStore
import com.vitran.shop.feature.location.domain.model.City
import com.vitran.shop.feature.location.domain.model.CityId
import com.vitran.shop.feature.location.domain.repository.LocationRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface AdminCitiesUiState {
    data object Loading : AdminCitiesUiState

    data class Content(val cities: List<City>) : AdminCitiesUiState

    data object Empty : AdminCitiesUiState

    data class Error(val error: AppError) : AdminCitiesUiState
}

class AdminCitiesViewModel(
    private val locationRepository: LocationRepository,
    sessionStateStore: AdminSessionStateStore,
) : ViewModel() {
    private val _uiState = MutableStateFlow<AdminCitiesUiState>(AdminCitiesUiState.Loading)
    val uiState: StateFlow<AdminCitiesUiState> = _uiState.asStateFlow()
    private val unregisterClear = sessionStateStore.registerClearCallback {
        _uiState.value = AdminCitiesUiState.Loading
    }

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = AdminCitiesUiState.Loading
            when (val result = locationRepository.getCities(forceRefresh = true)) {
                is AppResult.Success ->
                    _uiState.value =
                        if (result.value.isEmpty()) AdminCitiesUiState.Empty
                        else AdminCitiesUiState.Content(result.value)
                is AppResult.Failure -> _uiState.value = AdminCitiesUiState.Error(result.error)
            }
        }
    }

    override fun onCleared() {
        unregisterClear()
    }
}

data class AdminCityCreateUiState(
    val isSubmitting: Boolean = false,
    val createdCity: City? = null,
    val error: AppError? = null,
    val fieldErrors: Map<String, String> = emptyMap(),
)

class AdminCityCreateViewModel(
    private val repository: AdminLocationRepository,
    sessionStateStore: AdminSessionStateStore,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AdminCityCreateUiState())
    val uiState: StateFlow<AdminCityCreateUiState> = _uiState.asStateFlow()
    private val unregisterClear = sessionStateStore.registerClearCallback {
        _uiState.value = AdminCityCreateUiState()
    }
    private var submitJob: Job? = null

    fun create(slug: String, name: String) {
        if (_uiState.value.isSubmitting || submitJob?.isActive == true) return
        _uiState.update { it.copy(isSubmitting = true, createdCity = null, error = null, fieldErrors = emptyMap()) }
        submitJob =
            viewModelScope.launch {
                when (val result = repository.createCity(CreateCityCommand(slug.trim(), name.trim()))) {
                    is AppResult.Success ->
                        _uiState.update { it.copy(isSubmitting = false, createdCity = result.value) }
                    is AppResult.Failure -> {
                        val split = result.error.splitForForm(knownReasons = setOf("name", "slug"))
                        _uiState.update {
                            it.copy(
                                isSubmitting = false,
                                fieldErrors = split.fieldErrors,
                                error = when {
                                    split.generalMessage != null ->
                                        AppError.Validation(message = split.generalMessage)
                                    split.fieldErrors.isEmpty() -> result.error
                                    else -> null
                                },
                            )
                        }
                    }
                }
            }
    }

    fun clearFieldError(reason: String) {
        val key = reason.lowercase()
        _uiState.update { state ->
            if (key !in state.fieldErrors) state
            else state.copy(fieldErrors = state.fieldErrors - key)
        }
    }

    override fun onCleared() {
        submitJob?.cancel()
        unregisterClear()
    }
}

data class AdminCityDetailUiState(
    val isLoading: Boolean = true,
    val city: City? = null,
    val canDelete: Boolean = false,
    val isSubmitting: Boolean = false,
    val isDeleting: Boolean = false,
    val deleted: Boolean = false,
    val loadError: AppError? = null,
    val mutationError: AppError? = null,
    val deleteError: AdminLocationError? = null,
)

class AdminCityDetailViewModel(
    private val cityId: CityId,
    private val locationRepository: LocationRepository,
    private val adminLocationRepository: AdminLocationRepository,
    private val accountRepository: AccountRepository,
    private val permissions: AdminPermissions,
    sessionStateStore: AdminSessionStateStore,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow(AdminCityDetailUiState(canDelete = permissions.canDeleteCity(actorRoles())))
    val uiState: StateFlow<AdminCityDetailUiState> = _uiState.asStateFlow()
    private val unregisterClear = sessionStateStore.registerClearCallback {
        _uiState.value = AdminCityDetailUiState()
    }
    private var mutationJob: Job? = null

    init {
        viewModelScope.launch {
            accountRepository.currentUserState.collect {
                _uiState.update { state ->
                    state.copy(canDelete = permissions.canDeleteCity(actorRoles()))
                }
            }
        }
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, loadError = null, canDelete = permissions.canDeleteCity(actorRoles()))
            }
            when (val result = locationRepository.getCityById(cityId)) {
                is AppResult.Success -> _uiState.update { it.copy(isLoading = false, city = result.value) }
                is AppResult.Failure -> _uiState.update { it.copy(isLoading = false, loadError = result.error) }
            }
        }
    }

    fun update(slug: String, name: String) {
        if (_uiState.value.isSubmitting || _uiState.value.isDeleting || mutationJob?.isActive == true) return
        _uiState.update { it.copy(isSubmitting = true, mutationError = null) }
        mutationJob =
            viewModelScope.launch {
                val command = UpdateCityCommand(cityId, slug.trim(), name.trim())
                when (val result = adminLocationRepository.updateCity(command)) {
                    is AppResult.Success ->
                        _uiState.update { it.copy(isSubmitting = false, city = result.value) }
                    is AppResult.Failure ->
                        _uiState.update { it.copy(isSubmitting = false, mutationError = result.error) }
                }
            }
    }

    fun delete() {
        if (_uiState.value.isDeleting || _uiState.value.isSubmitting || mutationJob?.isActive == true) return
        val allowed = permissions.canDeleteCity(actorRoles())
        _uiState.update { it.copy(canDelete = allowed) }
        if (!allowed) {
            _uiState.update {
                it.copy(deleteError = AdminLocationError.RequestFailed(AppError.Forbidden()))
            }
            return
        }
        _uiState.update { it.copy(isDeleting = true, deleteError = null) }
        mutationJob =
            viewModelScope.launch {
                when (val result = adminLocationRepository.deleteCity(cityId)) {
                    is AppResult.Success ->
                        _uiState.update { it.copy(isDeleting = false, deleted = true) }
                    is AppResult.Failure ->
                        _uiState.update {
                            it.copy(
                                isDeleting = false,
                                deleteError =
                                    if (result.error is AppError.Conflict) {
                                        AdminLocationError.CityInUse
                                    } else {
                                        AdminLocationError.RequestFailed(result.error)
                                    },
                            )
                        }
                }
            }
    }

    private fun actorRoles() =
        (accountRepository.currentUserState.value as? CurrentUserState.Available)?.user?.roles.orEmpty()

    override fun onCleared() {
        mutationJob?.cancel()
        unregisterClear()
    }
}
