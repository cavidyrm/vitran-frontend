package com.vitran.shop.feature.location

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.feature.location.data.mapper.toDomain
import com.vitran.shop.feature.location.data.remote.LocationApi
import com.vitran.shop.feature.location.data.remote.dto.CityDto
import com.vitran.shop.feature.location.data.repository.DefaultLocationRepository
import com.vitran.shop.feature.location.domain.model.City
import com.vitran.shop.feature.location.domain.model.CityId
import com.vitran.shop.feature.location.domain.model.CitySlug
import io.ktor.client.engine.mock.MockEngine
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DefaultLocationRepositoryTest {

    private val environment = ApiEnvironment(origin = "http://localhost:8080")
    private val executor = createLocationTestExecutor()

    @Test
    fun cityDto_mapsToDomain() {
        val city = CityDto(id = 1, slug = "tehran", name = "تهران").toDomain()

        assertEquals(CityId(1), city.id)
        assertEquals(CitySlug("tehran"), city.slug)
        assertEquals("تهران", city.name)
    }

    @Test
    fun getCities_cachesSecondCall() = runTest {
        var requestCount = 0
        val repository = repository(
            MockEngine {
                requestCount++
                jsonResponse(HttpStatusCode.OK, citiesListEnvelope)
            },
        )

        val first = repository.getCities()
        val second = repository.getCities()

        assertIs<AppResult.Success<List<City>>>(first)
        assertIs<AppResult.Success<List<City>>>(second)
        assertEquals(1, requestCount)
        assertEquals(2, first.value.size)
    }

    @Test
    fun getCities_forceRefresh_hitsNetworkAgain() = runTest {
        var requestCount = 0
        val repository = repository(
            MockEngine {
                requestCount++
                jsonResponse(HttpStatusCode.OK, citiesListEnvelope)
            },
        )

        repository.getCities()
        repository.getCities(forceRefresh = true)

        assertEquals(2, requestCount)
    }

    @Test
    fun getCities_refreshFailure_keepsCache() = runTest {
        var requestCount = 0
        val repository = repository(
            MockEngine {
                requestCount++
                if (requestCount == 1) {
                    jsonResponse(HttpStatusCode.OK, citiesListEnvelope)
                } else {
                    jsonResponse(
                        HttpStatusCode.InternalServerError,
                        """{"success":false,"message":"fail","code":0,"errors":[]}""",
                    )
                }
            },
        )

        repository.getCities()
        val refreshResult = repository.getCities(forceRefresh = true)
        val cachedResult = repository.getCities()

        assertIs<AppResult.Success<List<City>>>(refreshResult)
        assertIs<AppResult.Success<List<City>>>(cachedResult)
        assertEquals(2, refreshResult.value.size)
        assertEquals(2, cachedResult.value.size)
    }

    @Test
    fun invalidateCities_clearsMemoryCache() = runTest {
        var requestCount = 0
        val repository = repository(
            MockEngine {
                requestCount++
                jsonResponse(HttpStatusCode.OK, citiesListEnvelope)
            },
        )

        repository.getCities()
        repository.invalidateCities()
        val afterInvalidate = repository.getCities()

        assertIs<AppResult.Success<List<City>>>(afterInvalidate)
        assertEquals(2, requestCount)
        assertEquals(2, afterInvalidate.value.size)
    }

    @Test
    fun getCityBySlug_returnsMappedCity() = runTest {
        val repository = repository(
            MockEngine { jsonResponse(HttpStatusCode.OK, cityDetailEnvelope) },
        )

        val result = repository.getCityBySlug(CitySlug("tehran"))

        assertIs<AppResult.Success<City>>(result)
        assertEquals("tehran", result.value.slug.value)
    }

    private fun repository(engine: MockEngine) =
        DefaultLocationRepository(
            locationApi = LocationApi(
                client = createLocationTestClient(engine),
                environment = environment,
                executor = executor,
            ),
        )
}
