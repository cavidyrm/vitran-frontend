package com.vitran.shop.feature.account.presentation.profile

import com.vitran.shop.core.domain.auth.UserRole
import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.account.domain.model.CurrentUserState
import com.vitran.shop.feature.account.domain.model.UpdateProfileCommand
import com.vitran.shop.feature.account.domain.model.User
import com.vitran.shop.feature.account.domain.repository.AccountRepository
import com.vitran.shop.feature.location.domain.model.City
import com.vitran.shop.feature.location.domain.model.CityId
import com.vitran.shop.feature.location.domain.model.CitySlug
import com.vitran.shop.feature.location.domain.repository.LocationRepository
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
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    @Test
    fun hydratesFullNameCityAndAvatar_thenSaveJoinsName() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val account = FakeAccountRepository(sampleUser(fullName = "علی محمدی", cityId = 1))
            val viewModel = ProfileViewModel(
                accountRepository = account,
                locationRepository = FakeLocationRepository(
                    cities = listOf(City(CityId(1), CitySlug("tehran"), "تهران")),
                ),
            )
            advanceUntilIdle()

            assertEquals("علی", viewModel.uiState.value.firstName)
            assertEquals("محمدی", viewModel.uiState.value.lastName)
            assertEquals(1L, viewModel.uiState.value.cityId)
            assertEquals("https://cdn.example/avatar.png", viewModel.uiState.value.avatarUrl)
            assertEquals(1, viewModel.uiState.value.cities.size)

            viewModel.onAction(ProfileUiAction.FirstNameChanged("جاوید"))
            viewModel.onAction(ProfileUiAction.Save)
            advanceUntilIdle()

            val command = account.lastCommand
            assertEquals("جاوید محمدی", command?.fullName)
            assertEquals(1L, command?.cityId)
            assertNull(command?.clearCityId)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun clearingCity_sendsClearFlagAndOmitsCityId() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val account = FakeAccountRepository(sampleUser(fullName = "Javid", cityId = 1))
            val viewModel = ProfileViewModel(
                accountRepository = account,
                locationRepository = FakeLocationRepository(),
            )
            advanceUntilIdle()

            viewModel.onAction(ProfileUiAction.CitySelected(null))
            viewModel.onAction(ProfileUiAction.Save)
            advanceUntilIdle()

            assertNull(viewModel.uiState.value.cityId)
            assertNull(account.lastCommand?.cityId)
            assertEquals(true, account.lastCommand?.clearCityId)
        } finally {
            Dispatchers.resetMain()
        }
    }

    private fun sampleUser(
        fullName: String?,
        cityId: Long?,
    ) = User(
        id = 2,
        phone = "9123456789",
        username = "javid",
        email = "user@example.com",
        roles = setOf(UserRole.User),
        verified = true,
        isActive = true,
        createdAt = Instant.parse("2026-01-01T00:00:00Z"),
        updatedAt = Instant.parse("2026-01-01T00:00:00Z"),
        fullName = fullName,
        avatarUrl = "https://cdn.example/avatar.png",
        cityId = cityId,
    )

    private class FakeAccountRepository(
        user: User,
    ) : AccountRepository {
        private val _state = MutableStateFlow<CurrentUserState>(CurrentUserState.Available(user))
        override val currentUserState: StateFlow<CurrentUserState> = _state
        var lastCommand: UpdateProfileCommand? = null

        override suspend fun refreshCurrentUser(): AppResult<User> {
            val user = (_state.value as CurrentUserState.Available).user
            return AppResult.Success(user)
        }

        override suspend fun updateProfile(command: UpdateProfileCommand): AppResult<User> {
            lastCommand = command
            val current = (_state.value as CurrentUserState.Available).user
            val updated = current.copy(
                username = command.username ?: current.username,
                email = command.email ?: current.email,
                fullName = command.fullName,
                avatarUrl = command.avatarUrl,
                cityId = if (command.clearCityId == true) null else command.cityId ?: current.cityId,
                city = if (command.clearCityId == true) null else current.city,
            )
            _state.value = CurrentUserState.Available(updated)
            return AppResult.Success(updated)
        }

        override suspend fun clear() {
            _state.value = CurrentUserState.Unknown
        }
    }

    private class FakeLocationRepository(
        var cities: List<City> = emptyList(),
    ) : LocationRepository {
        override suspend fun getCities(forceRefresh: Boolean): AppResult<List<City>> =
            AppResult.Success(cities)

        override suspend fun getCityById(id: CityId) =
            AppResult.Failure(AppError.Unexpected())

        override suspend fun getCityBySlug(slug: CitySlug) =
            AppResult.Failure(AppError.Unexpected())

        override suspend fun invalidateCities() = Unit
    }
}
