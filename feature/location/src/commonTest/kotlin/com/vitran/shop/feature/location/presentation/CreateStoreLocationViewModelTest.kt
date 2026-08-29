package com.vitran.shop.feature.location.presentation

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.location.domain.model.City
import com.vitran.shop.feature.location.domain.model.CityId
import com.vitran.shop.feature.location.domain.model.CitySlug
import com.vitran.shop.feature.location.domain.repository.LocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class CreateStoreLocationViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialLoad_success() = runTest {
        val viewModel = CreateStoreLocationViewModel(
            FakeLocationRepository(
                cities = listOf(
                    City(CityId(1), CitySlug("tehran"), "تهران"),
                ),
            ),
        )

        advanceUntilIdle()

        assertIs<CreateStoreLocationUiState.Content>(viewModel.uiState.value)
        assertEquals(1, (viewModel.uiState.value as CreateStoreLocationUiState.Content).cities.size)
    }

    @Test
    fun initialLoad_failure() = runTest {
        val viewModel = CreateStoreLocationViewModel(
            FakeLocationRepository(shouldFail = true),
        )

        advanceUntilIdle()

        assertIs<CreateStoreLocationUiState.Error>(viewModel.uiState.value)
    }

    @Test
    fun retry_afterFailure() = runTest {
        val repository = FakeLocationRepository(shouldFail = true)
        val viewModel = CreateStoreLocationViewModel(repository)

        advanceUntilIdle()
        repository.shouldFail = false
        repository.cities = listOf(
            City(CityId(1), CitySlug("tehran"), "تهران"),
        )
        viewModel.retry()
        advanceUntilIdle()

        assertIs<CreateStoreLocationUiState.Content>(viewModel.uiState.value)
    }

    private class FakeLocationRepository(
        var cities: List<City> = emptyList(),
        var shouldFail: Boolean = false,
    ) : LocationRepository {
        override suspend fun getCities(forceRefresh: Boolean): AppResult<List<City>> =
            if (shouldFail) {
                AppResult.Failure(com.vitran.shop.core.domain.error.AppError.Network.NoConnection())
            } else {
                AppResult.Success(cities)
            }

        override suspend fun getCityById(id: CityId) =
            AppResult.Failure(com.vitran.shop.core.domain.error.AppError.Unexpected())

        override suspend fun getCityBySlug(slug: CitySlug) =
            AppResult.Failure(com.vitran.shop.core.domain.error.AppError.Unexpected())
    }
}
