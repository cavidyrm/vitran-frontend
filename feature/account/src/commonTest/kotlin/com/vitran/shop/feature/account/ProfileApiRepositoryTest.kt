package com.vitran.shop.feature.account

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.serialization.createNetworkJson
import com.vitran.shop.feature.account.data.remote.ProfileApi
import com.vitran.shop.feature.account.data.remote.dto.SizeSlotAssignmentDto
import com.vitran.shop.feature.account.data.remote.dto.UpdateSizingRequestDto
import com.vitran.shop.feature.account.data.repository.DefaultProfileRepository
import com.vitran.shop.feature.account.domain.model.CreatePersonCommand
import com.vitran.shop.feature.account.domain.model.PersonId
import com.vitran.shop.feature.account.domain.model.PersonRelation
import com.vitran.shop.feature.account.domain.model.PersonSex
import com.vitran.shop.feature.account.domain.model.SizeSlotValue
import com.vitran.shop.feature.account.domain.model.SizingProfile
import com.vitran.shop.feature.account.domain.model.UpdatePersonCommand
import com.vitran.shop.feature.account.domain.model.UpdateSizingCommand
import io.ktor.client.engine.mock.MockEngine
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ProfileApiRepositoryTest {

    private val environment = ApiEnvironment(origin = "http://localhost:8080")
    private val executor = createAccountTestExecutor()

    @Test
    fun getSizingProfile_mapsSlotsAndObjectSizes() = runTest {
        val repository = profileRepository(
            MockEngine { request ->
                assertEquals(HttpMethod.Get, request.method)
                assertTrue(request.url.encodedPath.endsWith("/me/profile/sizing"))
                jsonResponse(HttpStatusCode.OK, sizingProfileBody)
            },
        )

        val result = repository.getSizingProfile()

        assertIs<AppResult.Success<SizingProfile>>(result)
        val profile = result.value
        assertEquals(true, profile.productMatchNotify)
        assertEquals(5, profile.slots.size)
        assertEquals("upper_body", profile.slots.first().slot)
        val self = profile.persons.single()
        assertEquals(PersonRelation.Self, self.relation)
        assertEquals(PersonSex.Male, self.sex)
        assertEquals("size", self.sizes["upper_body"]?.attributeSlug)
        assertEquals("size__m", self.sizes["upper_body"]?.valueSlug)
        assertEquals("shoe-size__42", self.sizes["shoes"]?.valueSlug)
    }

    @Test
    fun updateSizingProfile_sendsSelfRelationAndDecodesGetShape() = runTest {
        var bodyText = ""
        val repository = profileRepository(
            MockEngine { request ->
                assertEquals(HttpMethod.Put, request.method)
                bodyText = (request.body as TextContent).text
                jsonResponse(HttpStatusCode.OK, sizingProfileBody)
            },
        )

        val result = repository.updateSizingProfile(
            UpdateSizingCommand(
                name = "Me",
                sex = PersonSex.Male,
                notify = true,
                sizes = mapOf("upper_body" to SizeSlotValue("size__l")),
            ),
        )

        assertIs<AppResult.Success<*>>(result)
        assertTrue(bodyText.contains("\"relation\":\"self\""))
        assertTrue(bodyText.contains("\"upper_body\":\"size__l\""))
        assertTrue(bodyText.contains("\"name\":\"Me\""))
    }

    @Test
    fun sizeSlotAssignment_encodesStringOrObject() {
        val json = createNetworkJson()
        val asString = json.encodeToString(
            SizeSlotAssignmentDto.serializer(),
            SizeSlotAssignmentDto(valueSlug = "size__l"),
        )
        val asObject = json.encodeToString(
            SizeSlotAssignmentDto.serializer(),
            SizeSlotAssignmentDto(valueSlug = "size__32", attributeSlug = "size"),
        )
        val request = json.encodeToString(
            UpdateSizingRequestDto.serializer(),
            UpdateSizingRequestDto(
                sizes = mapOf(
                    "upper_body" to SizeSlotAssignmentDto("size__l"),
                    "lower_body" to SizeSlotAssignmentDto("size__32", "size"),
                ),
            ),
        )
        assertEquals("\"size__l\"", asString)
        assertTrue(asObject.contains("\"attribute_slug\":\"size\""))
        assertTrue(request.contains("\"upper_body\":\"size__l\""))
        assertTrue(request.contains("\"lower_body\":{"))
        assertTrue(request.contains("\"value_slug\":\"size__32\""))
    }

    @Test
    fun notifySettings_roundTrip() = runTest {
        val repository = profileRepository(
            MockEngine { request ->
                assertTrue(request.url.encodedPath.endsWith("/me/profile/notify"))
                jsonResponse(HttpStatusCode.OK, notifyBody)
            },
        )

        val getResult = repository.getNotifySettings()
        assertIs<AppResult.Success<*>>(getResult)
        assertEquals(true, (getResult as AppResult.Success).value.productMatchNotify)

        val putResult = repository.updateNotifySettings(true)
        assertIs<AppResult.Success<*>>(putResult)
        assertEquals(true, (putResult as AppResult.Success).value.productMatchNotify)
    }

    @Test
    fun listPersons_inferredWrapper() = runTest {
        val repository = profileRepository(
            MockEngine { jsonResponse(HttpStatusCode.OK, personsListBody) },
        )

        val result = repository.listPersons()

        assertIs<AppResult.Success<*>>(result)
        val persons = (result as AppResult.Success).value
        assertEquals(1, persons.size)
        assertEquals(PersonRelation.Partner, persons[0].relation)
        assertEquals("size__m", persons[0].sizes["upper_body"]?.valueSlug)
    }

    @Test
    fun createPerson_sendsPartnerRelationAndMixedSizes() = runTest {
        var bodyText = ""
        val repository = profileRepository(
            MockEngine { request ->
                assertEquals(HttpMethod.Post, request.method)
                bodyText = (request.body as TextContent).text
                jsonResponse(HttpStatusCode.OK, personBody)
            },
        )

        val result = repository.createPerson(
            CreatePersonCommand(
                name = "Partner",
                relation = PersonRelation.Partner,
                sex = PersonSex.Female,
                notify = true,
                sortOrder = 1,
                sizes = mapOf(
                    "upper_body" to SizeSlotValue("size__m"),
                    "lower_body" to SizeSlotValue("size__32", "size"),
                ),
            ),
        )

        assertIs<AppResult.Success<*>>(result)
        assertEquals(PersonId(3), (result as AppResult.Success).value.id)
        assertTrue(bodyText.contains("\"relation\":\"partner\""))
        assertTrue(bodyText.contains("\"upper_body\":\"size__m\""))
        assertTrue(bodyText.contains("\"attribute_slug\":\"size\""))
    }

    @Test
    fun updateAndDeletePerson() = runTest {
        val repository = profileRepository(
            MockEngine { request ->
                when (request.method) {
                    HttpMethod.Patch -> {
                        assertTrue(request.url.encodedPath.endsWith("/me/persons/3"))
                        jsonResponse(HttpStatusCode.OK, personBody)
                    }
                    HttpMethod.Delete -> {
                        assertTrue(request.url.encodedPath.endsWith("/me/persons/3"))
                        jsonResponse(HttpStatusCode.OK, emptyDataBody)
                    }
                    else -> error("Unexpected ${request.method}")
                }
            },
        )

        val updated = repository.updatePerson(
            UpdatePersonCommand(id = PersonId(3), name = "Partner Updated", notify = false),
        )
        assertIs<AppResult.Success<*>>(updated)

        val deleted = repository.deletePerson(PersonId(3))
        assertIs<AppResult.Success<Unit>>(deleted)
    }

    private fun profileRepository(engine: MockEngine) = DefaultProfileRepository(
        ProfileApi(
            client = createAccountTestClient(engine),
            environment = environment,
            executor = executor,
        ),
    )
}

private val sizingProfileBody = """
{
  "success": true,
  "message": "ok",
  "code": 1,
  "data": {
    "profile": {
      "product_match_notify": true,
      "persons": [
        {
          "id": 1,
          "name": "Me",
          "relation": "self",
          "sex": "male",
          "notify": true,
          "sort_order": 0,
          "sizes": {
            "upper_body": { "attribute_slug": "size", "value_slug": "size__m" },
            "shoes": { "attribute_slug": "shoe-size", "value_slug": "shoe-size__42" }
          }
        }
      ],
      "slots": [
        { "slot": "upper_body", "label": "Upper body size", "default_attribute_slug": "size", "sort_order": 1 },
        { "slot": "lower_body", "label": "Lower body size", "default_attribute_slug": "size", "sort_order": 2 },
        { "slot": "shoes", "label": "Shoe size", "default_attribute_slug": "shoe-size", "sort_order": 3 },
        { "slot": "mobile_model", "label": "Mobile model", "default_attribute_slug": "compatible-devices", "sort_order": 4 },
        { "slot": "car_model", "label": "Car model", "default_attribute_slug": "vehicle-fitment", "sort_order": 5 }
      ]
    }
  },
  "errors": []
}
""".trimIndent()

private val notifyBody = """
{
  "success": true,
  "message": "ok",
  "code": 1,
  "data": { "notify": { "product_match_notify": true } },
  "errors": []
}
""".trimIndent()

private val personsListBody = """
{
  "success": true,
  "message": "ok",
  "code": 1,
  "data": {
    "persons": [
      {
        "id": 3,
        "name": "Partner",
        "relation": "partner",
        "sex": "female",
        "notify": true,
        "sort_order": 1,
        "sizes": { "upper_body": "size__m" }
      }
    ]
  },
  "errors": []
}
""".trimIndent()

private val personBody = """
{
  "success": true,
  "message": "ok",
  "code": 1,
  "data": {
    "person": {
      "id": 3,
      "name": "Partner",
      "relation": "partner",
      "sex": "female",
      "notify": true,
      "sort_order": 1,
      "sizes": { "upper_body": "size__m" }
    }
  },
  "errors": []
}
""".trimIndent()

private val emptyDataBody = """
{
  "success": true,
  "message": "ok",
  "code": 1,
  "data": {},
  "errors": []
}
""".trimIndent()
