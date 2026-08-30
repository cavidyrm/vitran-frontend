package com.vitran.shop.feature.admin.catalog.location

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.feature.admin.catalog.location.data.remote.AdminLocationApi
import com.vitran.shop.feature.admin.catalog.location.data.repository.DefaultAdminLocationRepository
import com.vitran.shop.feature.admin.catalog.location.domain.CreateCityCommand
import com.vitran.shop.feature.admin.catalog.location.domain.UpdateCityCommand
import com.vitran.shop.feature.admin.createAdminTestClient
import com.vitran.shop.feature.admin.createAdminTestExecutor
import com.vitran.shop.feature.admin.jsonResponse
import com.vitran.shop.feature.location.domain.model.City
import com.vitran.shop.feature.location.domain.model.CityId
import com.vitran.shop.feature.location.domain.model.CitySlug
import com.vitran.shop.feature.location.domain.repository.LocationRepository
import io.ktor.client.engine.mock.MockEngine
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.coroutines.test.runTest

class AdminLocationApiRepositoryTest {
    private val environment = ApiEnvironment(origin = "http://localhost:8080")

    @Test
    fun createAndUpdateCity_serializeBodiesAndUseExpectedPaths() = runTest {
        var requestCount = 0
        val engine =
            MockEngine { request ->
                requestCount += 1
                assertEquals("Bearer ADMIN_ACCESS", request.headers[HttpHeaders.Authorization])
                when (requestCount) {
                    1 -> {
                        assertEquals(HttpMethod.Post, request.method)
                        assertEquals("/api/v1/admin/cities", request.url.encodedPath)
                        assertEquals(
                            """{"slug":"tehran","name":"تهران"}""",
                            (request.body as TextContent).text,
                        )
                    }
                    else -> {
                        assertEquals(HttpMethod.Patch, request.method)
                        assertEquals("/api/v1/admin/cities/7", request.url.encodedPath)
                        assertEquals(
                            """{"slug":"new-tehran","name":"تهران بزرگ"}""",
                            (request.body as TextContent).text,
                        )
                    }
                }
                jsonResponse(HttpStatusCode.OK, cityEnvelope)
            }
        val fakeLocation = FakeLocationRepository()
        val repository = createRepository(engine, fakeLocation)

        assertIs<AppResult.Success<*>>(
            repository.createCity(CreateCityCommand("tehran", "تهران")),
        )
        assertIs<AppResult.Success<*>>(
            repository.updateCity(UpdateCityCommand(CityId(7), "new-tehran", "تهران بزرگ")),
        )

        assertEquals(2, fakeLocation.invalidateCalls)
    }

    @Test
    fun deleteCity_usesIdPathAndMaps409ToConflictWithoutInvalidating() = runTest {
        val engine =
            MockEngine { request ->
                assertEquals(HttpMethod.Delete, request.method)
                assertEquals("/api/v1/admin/cities/7", request.url.encodedPath)
                jsonResponse(HttpStatusCode.Conflict, conflictEnvelope)
            }
        val fakeLocation = FakeLocationRepository()
        val result = createRepository(engine, fakeLocation).deleteCity(CityId(7))

        assertIs<AppResult.Failure>(result)
        assertIs<AppError.Conflict>(result.error)
        assertEquals(0, fakeLocation.invalidateCalls)
    }

    @Test
    fun deleteCity_successInvalidatesLocationCache() = runTest {
        val engine =
            MockEngine {
                jsonResponse(HttpStatusCode.OK, emptyEnvelope)
            }
        val fakeLocation = FakeLocationRepository()

        assertIs<AppResult.Success<*>>(createRepository(engine, fakeLocation).deleteCity(CityId(7)))
        assertEquals(1, fakeLocation.invalidateCalls)
    }

    private fun createRepository(
        engine: MockEngine,
        locationRepository: LocationRepository,
    ) = DefaultAdminLocationRepository(
        api = AdminLocationApi(createAdminTestClient(engine), environment, createAdminTestExecutor()),
        locationRepository = locationRepository,
    )

    private class FakeLocationRepository : LocationRepository {
        var invalidateCalls = 0

        override suspend fun getCities(forceRefresh: Boolean): AppResult<List<City>> =
            AppResult.Success(emptyList())

        override suspend fun getCityById(id: CityId): AppResult<City> =
            AppResult.Failure(AppError.NotFound())

        override suspend fun getCityBySlug(slug: CitySlug): AppResult<City> =
            AppResult.Failure(AppError.NotFound())

        override suspend fun invalidateCities() {
            invalidateCalls += 1
        }
    }

    private companion object {
        val cityEnvelope = """
            {
              "success": true,
              "message": "ok",
              "code": 1,
              "data": {"city":{"id":7,"slug":"tehran","name":"تهران"}},
              "errors": []
            }
        """.trimIndent()

        val emptyEnvelope = """
            {"success":true,"message":"ok","code":1,"data":{},"errors":[]}
        """.trimIndent()

        val conflictEnvelope = """
            {
              "success": false,
              "message": "city is in use",
              "code": 409,
              "data": {},
              "errors": [{"reason":"city_in_use","messages":["city is in use"]}]
            }
        """.trimIndent()
    }
}
