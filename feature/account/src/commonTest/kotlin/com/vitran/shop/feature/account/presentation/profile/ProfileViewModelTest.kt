package com.vitran.shop.feature.account.presentation.profile

import com.vitran.shop.core.domain.auth.UserRole
import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.platform.file.ImagePicker
import com.vitran.shop.core.platform.file.SelectedFile
import com.vitran.shop.feature.account.domain.model.CreatePersonCommand
import com.vitran.shop.feature.account.domain.model.CurrentUserState
import com.vitran.shop.feature.account.domain.model.Person
import com.vitran.shop.feature.account.domain.model.PersonId
import com.vitran.shop.feature.account.domain.model.PersonRelation
import com.vitran.shop.feature.account.domain.model.PersonSex
import com.vitran.shop.feature.account.domain.model.ProductMatchNotifySettings
import com.vitran.shop.feature.account.domain.model.SizeSlotValue
import com.vitran.shop.feature.account.domain.model.SizingProfile
import com.vitran.shop.feature.account.domain.model.UpdatePersonCommand
import com.vitran.shop.feature.account.domain.model.UpdateProfileCommand
import com.vitran.shop.feature.account.domain.model.UpdateSizingCommand
import com.vitran.shop.feature.account.domain.model.User
import com.vitran.shop.feature.account.domain.model.UsernameAvailability
import com.vitran.shop.feature.account.domain.repository.AccountRepository
import com.vitran.shop.feature.account.domain.repository.ProfileRepository
import com.vitran.shop.feature.location.domain.model.City
import com.vitran.shop.feature.location.domain.model.CityId
import com.vitran.shop.feature.location.domain.model.CitySlug
import com.vitran.shop.feature.location.domain.repository.LocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    @Test
    fun hydratesFullNameCityAndAvatar_thenSaveJoinsName() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val account = FakeAccountRepository(sampleUser(fullName = "علی محمدی", cityId = 1))
            val profileRepo = FakeProfileRepository()
            val viewModel = ProfileViewModel(
                accountRepository = account,
                locationRepository = FakeLocationRepository(
                    cities = listOf(City(CityId(1), CitySlug("tehran"), "تهران")),
                ),
                profileRepository = profileRepo,
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
            assertTrue(profileRepo.lastSizingCommand != null)
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
                profileRepository = FakeProfileRepository(),
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

    @Test
    fun hydratesSizingAndGender_thenSaveSendsUpdateSizingCommand() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val profileRepo = FakeProfileRepository(
                sizing = SizingProfile(
                    productMatchNotify = true,
                    persons = listOf(
                        Person(
                            id = PersonId(1),
                            name = "Me",
                            relation = PersonRelation.Self,
                            sex = PersonSex.Male,
                            notify = true,
                            sortOrder = 0,
                            sizes = mapOf(
                                "upper_body" to SizeSlotValue("size__m", "size"),
                                "lower_body" to SizeSlotValue("size__32", "size"),
                                "shoes" to SizeSlotValue("shoe-size__42", "shoe-size"),
                            ),
                        ),
                    ),
                    slots = emptyList(),
                ),
            )
            val account = FakeAccountRepository(sampleUser(fullName = "Javid", cityId = null))
            val viewModel = ProfileViewModel(
                accountRepository = account,
                locationRepository = FakeLocationRepository(),
                profileRepository = profileRepo,
            )
            advanceUntilIdle()

            assertEquals(ProfileGender.Male, viewModel.uiState.value.gender)
            assertEquals("size__m", viewModel.uiState.value.upperBodySize)
            assertEquals("size__32", viewModel.uiState.value.lowerBodySize)
            assertEquals("shoe-size__42", viewModel.uiState.value.shoeSize)

            viewModel.onAction(ProfileUiAction.GenderChanged(ProfileGender.Female))
            viewModel.onAction(ProfileUiAction.UpperBodySizeChanged("size__l"))
            viewModel.onAction(ProfileUiAction.ShoeSizeChanged(null))
            viewModel.onAction(ProfileUiAction.Save)
            advanceUntilIdle()

            val sizing = profileRepo.lastSizingCommand
            assertEquals(PersonSex.Female, sizing?.sex)
            assertEquals("Me", sizing?.name)
            assertEquals(true, sizing?.notify)
            assertEquals("size__l", sizing?.sizes?.get("upper_body")?.valueSlug)
            assertEquals("size__32", sizing?.sizes?.get("lower_body")?.valueSlug)
            assertNull(sizing?.sizes?.get("shoes"))
            assertEquals(PersonSex.Female, account.lastCommand?.sex)
            assertNull(account.lastCommand?.clearSex)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun unspecifiedGender_sendsClearSexOnProfile() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val account = FakeAccountRepository(
                sampleUser(fullName = "Javid", cityId = null, sex = PersonSex.Male),
            )
            val viewModel = ProfileViewModel(
                accountRepository = account,
                locationRepository = FakeLocationRepository(),
                profileRepository = FakeProfileRepository(),
            )
            advanceUntilIdle()

            viewModel.onAction(ProfileUiAction.GenderChanged(ProfileGender.Unspecified))
            viewModel.onAction(ProfileUiAction.Save)
            advanceUntilIdle()

            assertNull(account.lastCommand?.sex)
            assertEquals(true, account.lastCommand?.clearSex)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun usernameCheck_debouncesAndFiresOnce() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val account = FakeAccountRepository(
                sampleUser(fullName = "Javid", cityId = null),
                usernameAvailable = true,
            )
            val viewModel = ProfileViewModel(
                accountRepository = account,
                locationRepository = FakeLocationRepository(),
                profileRepository = FakeProfileRepository(),
                usernameDebounceMs = 400L,
            )
            advanceUntilIdle()

            viewModel.onAction(ProfileUiAction.UsernameChanged("ja"))
            viewModel.onAction(ProfileUiAction.UsernameChanged("jav"))
            viewModel.onAction(ProfileUiAction.UsernameChanged("javi"))
            viewModel.onAction(ProfileUiAction.UsernameChanged("javix"))
            advanceTimeBy(399)
            assertEquals(0, account.usernameCheckCalls)
            assertIs<UsernameCheckUiStatus.Checking>(viewModel.uiState.value.usernameCheck)

            advanceTimeBy(1)
            advanceUntilIdle()
            assertEquals(1, account.usernameCheckCalls)
            assertEquals("javix", account.lastUsernameChecked)
            assertIs<UsernameCheckUiStatus.Available>(viewModel.uiState.value.usernameCheck)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun usernameCheck_skipsWhenUnchangedFromLoaded() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val account = FakeAccountRepository(sampleUser(fullName = "Javid", cityId = null))
            val viewModel = ProfileViewModel(
                accountRepository = account,
                locationRepository = FakeLocationRepository(),
                profileRepository = FakeProfileRepository(),
                usernameDebounceMs = 400L,
            )
            advanceUntilIdle()

            viewModel.onAction(ProfileUiAction.UsernameChanged("javid"))
            advanceTimeBy(500)
            advanceUntilIdle()
            assertEquals(0, account.usernameCheckCalls)
            assertIs<UsernameCheckUiStatus.Idle>(viewModel.uiState.value.usernameCheck)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun usernameTaken_blocksSave() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val account = FakeAccountRepository(
                sampleUser(fullName = "Javid", cityId = null),
                usernameAvailable = false,
            )
            val viewModel = ProfileViewModel(
                accountRepository = account,
                locationRepository = FakeLocationRepository(),
                profileRepository = FakeProfileRepository(),
                usernameDebounceMs = 400L,
            )
            advanceUntilIdle()

            viewModel.onAction(ProfileUiAction.UsernameChanged("taken"))
            advanceTimeBy(400)
            advanceUntilIdle()
            assertIs<UsernameCheckUiStatus.Taken>(viewModel.uiState.value.usernameCheck)

            viewModel.onAction(ProfileUiAction.Save)
            advanceUntilIdle()
            assertNull(account.lastCommand)
            assertTrue(viewModel.uiState.value.error != null)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun birthdayChange_staysLocalInUiState() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val viewModel = ProfileViewModel(
                accountRepository = FakeAccountRepository(sampleUser(fullName = "Javid", cityId = null)),
                locationRepository = FakeLocationRepository(),
                profileRepository = FakeProfileRepository(),
            )
            advanceUntilIdle()

            viewModel.onAction(ProfileUiAction.BirthdayChanged("1990-03-21"))
            assertEquals("1990-03-21", viewModel.uiState.value.birthdayIso)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun hydratesCityFromUserAndCitiesList() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val account = FakeAccountRepository(
                sampleUser(
                    fullName = "Javid",
                    cityId = 1,
                    cityName = "تهران",
                ),
            )
            val viewModel = ProfileViewModel(
                accountRepository = account,
                locationRepository = FakeLocationRepository(
                    cities = listOf(
                        City(CityId(2), CitySlug("isfahan"), "اصفهان"),
                        City(CityId(1), CitySlug("tehran"), "تهران"),
                    ),
                ),
                profileRepository = FakeProfileRepository(),
            )
            advanceUntilIdle()

            assertEquals(1L, viewModel.uiState.value.cityId)
            assertEquals("تهران", viewModel.uiState.value.cityName)
            assertEquals(2, viewModel.uiState.value.cities.size)
            assertEquals(
                listOf("اصفهان", "تهران"),
                viewModel.uiState.value.cities.map { it.name },
            )
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun pickAvatar_cancelLeavesPreviewUnchanged() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val viewModel = ProfileViewModel(
                accountRepository = FakeAccountRepository(sampleUser(fullName = "Javid", cityId = null)),
                locationRepository = FakeLocationRepository(),
                profileRepository = FakeProfileRepository(),
                imagePicker = FakeImagePicker(emptyList()),
            )
            advanceUntilIdle()

            viewModel.onAction(ProfileUiAction.PickAvatar)
            advanceUntilIdle()

            assertNull(viewModel.uiState.value.avatarPreviewBytes)
            assertEquals(false, viewModel.uiState.value.isPickingAvatar)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun pickAvatar_setsLocalPreview_andSaveKeepsExistingAvatarUrl() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val preview = byteArrayOf(1, 2, 3, 4)
            val account = FakeAccountRepository(sampleUser(fullName = "Javid", cityId = null))
            val viewModel = ProfileViewModel(
                accountRepository = account,
                locationRepository = FakeLocationRepository(),
                profileRepository = FakeProfileRepository(),
                imagePicker = FakeImagePicker(
                    listOf(
                        SelectedFile.fromBytes("avatar.jpg", preview, "image/jpeg"),
                    ),
                ),
            )
            advanceUntilIdle()

            viewModel.onAction(ProfileUiAction.PickAvatar)
            advanceUntilIdle()

            assertTrue(viewModel.uiState.value.avatarPreviewBytes.contentEquals(preview))

            viewModel.onAction(ProfileUiAction.Save)
            advanceUntilIdle()

            assertEquals("https://cdn.example/avatar.png", account.lastCommand?.avatarUrl)
        } finally {
            Dispatchers.resetMain()
        }
    }

    private fun sampleUser(
        fullName: String?,
        cityId: Long?,
        cityName: String? = null,
        sex: PersonSex? = null,
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
        sex = sex,
        cityId = cityId,
        city = cityId?.let { id ->
            cityName?.let {
                com.vitran.shop.feature.account.domain.model.UserCity(
                    id = id,
                    slug = "city-$id",
                    name = it,
                )
            }
        },
    )

    private class FakeAccountRepository(
        user: User,
        private val usernameAvailable: Boolean = true,
    ) : AccountRepository {
        private val _state = MutableStateFlow<CurrentUserState>(CurrentUserState.Available(user))
        override val currentUserState: StateFlow<CurrentUserState> = _state
        var lastCommand: UpdateProfileCommand? = null
        var usernameCheckCalls = 0
        var lastUsernameChecked: String? = null

        override suspend fun refreshCurrentUser(): AppResult<User> {
            val user = (_state.value as CurrentUserState.Available).user
            return AppResult.Success(user)
        }

        override suspend fun checkUsernameAvailability(username: String): AppResult<UsernameAvailability> {
            usernameCheckCalls += 1
            lastUsernameChecked = username
            return AppResult.Success(
                UsernameAvailability(username = username, isAvailable = usernameAvailable),
            )
        }

        override suspend fun updateProfile(command: UpdateProfileCommand): AppResult<User> {
            lastCommand = command
            val current = (_state.value as CurrentUserState.Available).user
            val updated = current.copy(
                username = command.username ?: current.username,
                email = command.email ?: current.email,
                fullName = command.fullName,
                avatarUrl = if (command.clearAvatarUrl == true) null else command.avatarUrl ?: current.avatarUrl,
                sex = if (command.clearSex == true) null else command.sex ?: current.sex,
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

    private class FakeProfileRepository(
        private var sizing: SizingProfile = SizingProfile(
            productMatchNotify = false,
            persons = emptyList(),
            slots = emptyList(),
        ),
    ) : ProfileRepository {
        var lastSizingCommand: UpdateSizingCommand? = null

        override suspend fun getSizingProfile(): AppResult<SizingProfile> =
            AppResult.Success(sizing)

        override suspend fun updateSizingProfile(command: UpdateSizingCommand): AppResult<SizingProfile> {
            lastSizingCommand = command
            val self = sizing.persons.firstOrNull { it.relation is PersonRelation.Self }
            val updatedSelf = (self ?: Person(
                id = PersonId(1),
                name = command.name,
                relation = PersonRelation.Self,
                sex = command.sex,
                notify = command.notify ?: false,
                sortOrder = 0,
                sizes = emptyMap(),
            )).copy(
                name = command.name ?: self?.name,
                sex = command.sex ?: self?.sex,
                notify = command.notify ?: self?.notify ?: false,
                sizes = command.sizes.ifEmpty { self?.sizes.orEmpty() },
            )
            sizing = sizing.copy(
                persons = listOf(updatedSelf) + sizing.persons.filterNot { it.relation is PersonRelation.Self },
            )
            return AppResult.Success(sizing)
        }

        override suspend fun getNotifySettings(): AppResult<ProductMatchNotifySettings> =
            AppResult.Failure(AppError.Unexpected())

        override suspend fun updateNotifySettings(productMatchNotify: Boolean) =
            AppResult.Failure(AppError.Unexpected())

        override suspend fun listPersons(): AppResult<List<Person>> =
            AppResult.Failure(AppError.Unexpected())

        override suspend fun getPerson(id: PersonId): AppResult<Person> =
            AppResult.Failure(AppError.Unexpected())

        override suspend fun createPerson(command: CreatePersonCommand): AppResult<Person> =
            AppResult.Failure(AppError.Unexpected())

        override suspend fun updatePerson(command: UpdatePersonCommand): AppResult<Person> =
            AppResult.Failure(AppError.Unexpected())

        override suspend fun deletePerson(id: PersonId): AppResult<Unit> =
            AppResult.Failure(AppError.Unexpected())
    }

    private class FakeImagePicker(
        private val files: List<SelectedFile>,
    ) : ImagePicker {
        override suspend fun pickImages(maxCount: Int): List<SelectedFile> = files.take(maxCount)
    }
}
