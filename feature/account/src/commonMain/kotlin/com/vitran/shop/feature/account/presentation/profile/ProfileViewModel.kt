package com.vitran.shop.feature.account.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.account.domain.model.CurrentUserState
import com.vitran.shop.feature.account.domain.model.PersonRelation
import com.vitran.shop.feature.account.domain.model.PersonSex
import com.vitran.shop.feature.account.domain.model.SizeSlotValue
import com.vitran.shop.feature.account.domain.model.SizingProfile
import com.vitran.shop.feature.account.domain.model.UpdateProfileCommand
import com.vitran.shop.feature.account.domain.model.UpdateSizingCommand
import com.vitran.shop.feature.account.domain.model.User
import com.vitran.shop.feature.account.domain.model.joinFullName
import com.vitran.shop.feature.account.domain.model.splitFullName
import com.vitran.shop.feature.account.domain.repository.AccountRepository
import com.vitran.shop.feature.account.domain.repository.ProfileRepository
import com.vitran.shop.feature.location.domain.repository.LocationRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileCityOption(
    val id: Long,
    val name: String,
)

enum class ProfileGender {
    Unspecified,
    Female,
    Male,
    Other,
}

sealed class UsernameCheckUiStatus {
    data object Idle : UsernameCheckUiStatus()
    data object Checking : UsernameCheckUiStatus()
    data class Available(val username: String) : UsernameCheckUiStatus()
    data class Taken(val username: String) : UsernameCheckUiStatus()
    data class Error(val error: AppError) : UsernameCheckUiStatus()
}

data class ProfileUiState(
    val isLoading: Boolean = true,
    val isUpdating: Boolean = false,
    val isCitiesLoading: Boolean = false,
    val username: String = "",
    val email: String = "",
    val phone: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val avatarUrl: String = "",
    val cityId: Long? = null,
    val cityName: String? = null,
    val cityCleared: Boolean = false,
    val cities: List<ProfileCityOption> = emptyList(),
    val citiesError: String? = null,
    val birthdayIso: String? = null,
    val gender: ProfileGender = ProfileGender.Unspecified,
    val upperBodySize: String? = null,
    val lowerBodySize: String? = null,
    val shoeSize: String? = null,
    val sizingError: String? = null,
    val usernameCheck: UsernameCheckUiStatus = UsernameCheckUiStatus.Idle,
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
    data class BirthdayChanged(val iso: String?) : ProfileUiAction
    data class GenderChanged(val gender: ProfileGender) : ProfileUiAction
    data class UpperBodySizeChanged(val slug: String?) : ProfileUiAction
    data class LowerBodySizeChanged(val slug: String?) : ProfileUiAction
    data class ShoeSizeChanged(val slug: String?) : ProfileUiAction
    data object ToggleAvatarUrlField : ProfileUiAction
    data object Retry : ProfileUiAction
    data object Save : ProfileUiAction
}

class ProfileViewModel(
    private val accountRepository: AccountRepository,
    private val locationRepository: LocationRepository,
    private val profileRepository: ProfileRepository,
    private val usernameDebounceMs: Long = 400L,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private var sizingSelfName: String? = null
    private var sizingNotify: Boolean? = null
    private var loadedUsername: String = ""
    private var usernameCheckJob: Job? = null

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
        loadSizing()
    }

    fun onAction(action: ProfileUiAction) {
        when (action) {
            is ProfileUiAction.UsernameChanged -> onUsernameChanged(action.value)
            is ProfileUiAction.EmailChanged -> _uiState.update { it.copy(email = action.value) }
            is ProfileUiAction.FirstNameChanged -> _uiState.update { it.copy(firstName = action.value) }
            is ProfileUiAction.LastNameChanged -> _uiState.update { it.copy(lastName = action.value) }
            is ProfileUiAction.AvatarUrlChanged -> _uiState.update { it.copy(avatarUrl = action.value) }
            is ProfileUiAction.CitySelected -> _uiState.update { state ->
                val name = action.cityId?.let { id ->
                    state.cities.firstOrNull { it.id == id }?.name
                }
                state.copy(
                    cityId = action.cityId,
                    cityName = name,
                    cityCleared = action.cityId == null,
                )
            }
            is ProfileUiAction.BirthdayChanged -> _uiState.update { it.copy(birthdayIso = action.iso) }
            is ProfileUiAction.GenderChanged -> _uiState.update { it.copy(gender = action.gender) }
            is ProfileUiAction.UpperBodySizeChanged ->
                _uiState.update { it.copy(upperBodySize = action.slug) }
            is ProfileUiAction.LowerBodySizeChanged ->
                _uiState.update { it.copy(lowerBodySize = action.slug) }
            is ProfileUiAction.ShoeSizeChanged ->
                _uiState.update { it.copy(shoeSize = action.slug) }
            ProfileUiAction.ToggleAvatarUrlField -> _uiState.update {
                it.copy(showAvatarUrlField = !it.showAvatarUrlField)
            }
            ProfileUiAction.Retry -> {
                refresh()
                loadCities()
                loadSizing()
            }
            ProfileUiAction.Save -> save()
        }
    }

    private fun onUsernameChanged(raw: String) {
        usernameCheckJob?.cancel()
        val trimmed = raw.trim()
        _uiState.update { it.copy(username = raw) }
        if (trimmed.isEmpty() ||
            trimmed.length !in USERNAME_MIN_LENGTH..USERNAME_MAX_LENGTH ||
            trimmed.equals(loadedUsername, ignoreCase = false)
        ) {
            _uiState.update { it.copy(usernameCheck = UsernameCheckUiStatus.Idle) }
            return
        }
        usernameCheckJob =
            viewModelScope.launch {
                _uiState.update { it.copy(usernameCheck = UsernameCheckUiStatus.Checking) }
                delay(usernameDebounceMs)
                when (val result = accountRepository.checkUsernameAvailability(trimmed)) {
                    is AppResult.Success -> {
                        if (_uiState.value.username.trim() != trimmed) return@launch
                        val availability = result.value
                        _uiState.update {
                            it.copy(
                                usernameCheck =
                                    if (availability.isAvailable) {
                                        UsernameCheckUiStatus.Available(availability.username)
                                    } else {
                                        UsernameCheckUiStatus.Taken(availability.username)
                                    },
                            )
                        }
                    }
                    is AppResult.Failure -> {
                        if (_uiState.value.username.trim() != trimmed) return@launch
                        _uiState.update {
                            it.copy(usernameCheck = UsernameCheckUiStatus.Error(result.error))
                        }
                    }
                }
            }
    }

    private fun applyUser(user: User) {
        val (firstName, lastName) = splitFullName(user.fullName)
        val cityId = user.cityId ?: user.city?.id
        val cityName = user.city?.name
            ?: _uiState.value.cities.firstOrNull { it.id == cityId }?.name
        loadedUsername = user.username.orEmpty()
        usernameCheckJob?.cancel()
        _uiState.update { state ->
            state.copy(
                isLoading = false,
                username = user.username.orEmpty(),
                email = user.email.orEmpty(),
                phone = user.phone,
                firstName = firstName,
                lastName = lastName,
                avatarUrl = user.avatarUrl.orEmpty(),
                cityId = cityId,
                cityName = cityName,
                cityCleared = false,
                cities = ensureSelectedCityInList(state.cities, cityId, cityName),
                gender = user.sex.toProfileGender().takeIf { it != ProfileGender.Unspecified }
                    ?: state.gender,
                usernameCheck = UsernameCheckUiStatus.Idle,
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
            _uiState.update { it.copy(isCitiesLoading = true, citiesError = null) }
            when (val result = locationRepository.getCities()) {
                is AppResult.Success -> {
                    val loaded = result.value
                        .map { city -> ProfileCityOption(id = city.id.value, name = city.name) }
                        .sortedBy { it.name }
                    _uiState.update { state ->
                        val cityId = state.cityId
                        val resolvedName = cityId?.let { id ->
                            loaded.firstOrNull { it.id == id }?.name ?: state.cityName
                        }
                        state.copy(
                            isCitiesLoading = false,
                            cities = ensureSelectedCityInList(loaded, cityId, resolvedName),
                            cityName = resolvedName,
                            citiesError = null,
                        )
                    }
                }
                is AppResult.Failure -> _uiState.update {
                    it.copy(
                        isCitiesLoading = false,
                        citiesError = result.error.message ?: "خطا در دریافت لیست شهرها",
                    )
                }
            }
        }
    }

    private fun ensureSelectedCityInList(
        cities: List<ProfileCityOption>,
        cityId: Long?,
        cityName: String?,
    ): List<ProfileCityOption> {
        if (cityId == null) return cities
        if (cities.any { it.id == cityId }) return cities
        val label = cityName?.takeIf { it.isNotBlank() } ?: return cities
        return (listOf(ProfileCityOption(id = cityId, name = label)) + cities)
            .sortedBy { it.name }
    }

    private fun loadSizing() {
        viewModelScope.launch {
            when (val result = profileRepository.getSizingProfile()) {
                is AppResult.Success -> applySizing(result.value)
                is AppResult.Failure -> _uiState.update {
                    it.copy(sizingError = result.error.message)
                }
            }
        }
    }

    private fun applySizing(profile: SizingProfile) {
        val self = profile.persons.firstOrNull { it.relation is PersonRelation.Self }
            ?: profile.persons.firstOrNull()
        sizingSelfName = self?.name
        sizingNotify = self?.notify
        val sizingGender = self?.sex.toProfileGender()
        _uiState.update {
            it.copy(
                gender = if (sizingGender != ProfileGender.Unspecified) {
                    sizingGender
                } else {
                    it.gender
                },
                upperBodySize = self?.sizes?.get(SLOT_UPPER)?.valueSlug,
                lowerBodySize = self?.sizes?.get(SLOT_LOWER)?.valueSlug,
                shoeSize = self?.sizes?.get(SLOT_SHOES)?.valueSlug,
                sizingError = null,
            )
        }
    }

    private fun save() {
        val state = _uiState.value
        if (state.isUpdating) return
        if (state.usernameCheck is UsernameCheckUiStatus.Taken) {
            _uiState.update {
                it.copy(error = "این نام کاربری قبلاً استفاده شده است.")
            }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, error = null) }
            when (
                val profileResult = accountRepository.updateProfile(
                    UpdateProfileCommand(
                        username = state.username.ifBlank { null },
                        email = state.email.ifBlank { null },
                        fullName = joinFullName(state.firstName, state.lastName),
                        avatarUrl = state.avatarUrl.ifBlank { null },
                        sex = state.gender.toPersonSex(),
                        cityId = state.cityId,
                        clearCityId = true.takeIf { state.cityCleared },
                        clearSex = true.takeIf { state.gender == ProfileGender.Unspecified },
                    ),
                )
            ) {
                is AppResult.Failure -> {
                    _uiState.update {
                        it.copy(isUpdating = false, error = profileResult.error.message)
                    }
                    return@launch
                }
                is AppResult.Success -> {
                    val user = profileResult.value
                    loadedUsername = user.username.orEmpty()
                    val cityId = user.cityId ?: user.city?.id
                    val cityName = user.city?.name
                        ?: _uiState.value.cities.firstOrNull { it.id == cityId }?.name
                    _uiState.update { state ->
                        state.copy(
                            username = user.username.orEmpty(),
                            cityId = cityId,
                            cityName = cityName,
                            cityCleared = false,
                            cities = ensureSelectedCityInList(state.cities, cityId, cityName),
                            usernameCheck = UsernameCheckUiStatus.Idle,
                        )
                    }
                }
            }

            val sizes = buildMap {
                state.upperBodySize?.let { put(SLOT_UPPER, SizeSlotValue(it, ATTR_SIZE)) }
                state.lowerBodySize?.let { put(SLOT_LOWER, SizeSlotValue(it, ATTR_SIZE)) }
                state.shoeSize?.let { put(SLOT_SHOES, SizeSlotValue(it, ATTR_SHOE)) }
            }
            when (
                val sizingResult = profileRepository.updateSizingProfile(
                    UpdateSizingCommand(
                        name = sizingSelfName,
                        sex = state.gender.toPersonSex(),
                        notify = sizingNotify,
                        sizes = sizes,
                    ),
                )
            ) {
                is AppResult.Success -> {
                    applySizing(sizingResult.value)
                    _uiState.update {
                        it.copy(isUpdating = false, showAvatarUrlField = false)
                    }
                }
                is AppResult.Failure -> _uiState.update {
                    it.copy(isUpdating = false, error = sizingResult.error.message)
                }
            }
        }
    }

    private companion object {
        const val SLOT_UPPER = "upper_body"
        const val SLOT_LOWER = "lower_body"
        const val SLOT_SHOES = "shoes"
        const val ATTR_SIZE = "size"
        const val ATTR_SHOE = "shoe-size"
        const val USERNAME_MIN_LENGTH = 3
        const val USERNAME_MAX_LENGTH = 50
    }
}

private fun PersonSex?.toProfileGender(): ProfileGender = when (this) {
    PersonSex.Male -> ProfileGender.Male
    PersonSex.Female -> ProfileGender.Female
    is PersonSex.Unknown, null -> ProfileGender.Unspecified
}

private fun ProfileGender.toPersonSex(): PersonSex? = when (this) {
    ProfileGender.Male -> PersonSex.Male
    ProfileGender.Female -> PersonSex.Female
    ProfileGender.Unspecified, ProfileGender.Other -> null
}
