package com.vitran.shop.feature.location

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.feature.location.data.remote.LocationApi
import com.vitran.shop.feature.location.data.remote.dto.CitiesDataDto
import com.vitran.shop.feature.location.data.remote.dto.CityDataDto
import com.vitran.shop.feature.location.domain.model.CityId
import com.vitran.shop.feature.location.domain.model.CitySlug
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondError
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class LocationApiTest {

    private val environment = ApiEnvironment(origin = "http://localhost:8080")
    private val executor = createLocationTestExecutor()

    @Test
    fun getCities_usesPublicPath_andNoAuthorizationHeader() = runTest {
        var capturedPath: String? = null
        var authorization: String? = null
        val api = LocationApi(
            client = createLocationTestClient(
                MockEngine { request ->
                    capturedPath = request.url.encodedPath
                    authorization = request.headers[HttpHeaders.Authorization]
                    jsonResponse(HttpStatusCode.OK, citiesListEnvelope)
                },
            ),
            environment = environment,
            executor = executor,
        )

        val result = api.getCities()

        assertIs<AppResult.Success<CitiesDataDto>>(result)
        assertEquals("/api/v1/cities", capturedPath)
        assertNull(authorization)
        assertEquals(2, result.value.cities.size)
        assertEquals("tehran", result.value.cities.first().slug)
    }

    @Test
    fun getCityById_mapsDataCityWrapper() = runTest {
        val api = LocationApi(
            client = createLocationTestClient(
                MockEngine { request ->
                    assertEquals("/api/v1/cities/1", request.url.encodedPath)
                    jsonResponse(HttpStatusCode.OK, cityDetailEnvelope)
                },
            ),
            environment = environment,
            executor = executor,
        )

        val result = api.getCityById(CityId(1))

        assertIs<AppResult.Success<CityDataDto>>(result)
        assertEquals("تهران", result.value.city.name)
    }

    @Test
    fun getCityBySlug_encodesSlugPath() = runTest {
        val api = LocationApi(
            client = createLocationTestClient(
                MockEngine { request ->
                    assertEquals("/api/v1/cities/slug/tehran", request.url.encodedPath)
                    jsonResponse(HttpStatusCode.OK, cityDetailEnvelope)
                },
            ),
            environment = environment,
            executor = executor,
        )

        val result = api.getCityBySlug(CitySlug("tehran"))

        assertIs<AppResult.Success<CityDataDto>>(result)
        assertEquals(1, result.value.city.id)
    }

    @Test
    fun getCityById_notFound_mapsFailure() = runTest {
        val api = LocationApi(
            client = createLocationTestClient(
                MockEngine { respondError(HttpStatusCode.NotFound) },
            ),
            environment = environment,
            executor = executor,
        )

        val result = api.getCityById(CityId(999))

        assertIs<AppResult.Failure>(result)
    }
}
