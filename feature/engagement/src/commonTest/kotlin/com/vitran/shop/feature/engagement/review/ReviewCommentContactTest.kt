package com.vitran.shop.feature.engagement.review

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.serialization.createNetworkJson
import com.vitran.shop.feature.engagement.ContactWhatsAppEnvelope
import com.vitran.shop.feature.engagement.PublicCommentsEnvelope
import com.vitran.shop.feature.engagement.RecordingAnalyticsTracker
import com.vitran.shop.feature.engagement.ReviewsEnvelope
import com.vitran.shop.feature.engagement.SubmittedCommentEnvelope
import com.vitran.shop.feature.engagement.SubmittedReviewEnvelope
import com.vitran.shop.feature.engagement.comment.data.remote.ShopCommentApi
import com.vitran.shop.feature.engagement.comment.data.repository.DefaultShopCommentRepository
import com.vitran.shop.feature.engagement.comment.domain.usecase.SubmitShopCommentUseCase
import com.vitran.shop.feature.engagement.contactUnsupportedEnvelope
import com.vitran.shop.feature.engagement.contact.data.remote.ProductContactApi
import com.vitran.shop.feature.engagement.contact.data.repository.DefaultProductContactRepository
import com.vitran.shop.feature.engagement.contact.domain.model.ContactRoute
import com.vitran.shop.feature.engagement.contact.domain.model.PurchaseIntentId
import com.vitran.shop.feature.engagement.contact.domain.usecase.ContactProductUseCase
import com.vitran.shop.feature.engagement.createEngagementTestClient
import com.vitran.shop.feature.engagement.createEngagementTestExecutor
import com.vitran.shop.feature.engagement.engagementEnvironment
import com.vitran.shop.feature.engagement.jsonResponse
import com.vitran.shop.feature.engagement.review.data.remote.ProductReviewApi
import com.vitran.shop.feature.engagement.review.data.remote.dto.CreateReviewRequestDto
import com.vitran.shop.feature.engagement.review.data.repository.DefaultProductReviewRepository
import com.vitran.shop.feature.engagement.review.domain.model.Rating as ReviewRating
import com.vitran.shop.feature.engagement.review.domain.usecase.SubmitProductReviewUseCase
import com.vitran.shop.feature.engagement.session.DefaultVisitorSessionProvider
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import io.ktor.client.engine.mock.MockEngine
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ReviewCommentContactTest {

    @Test
    fun rating_accepts1And5_rejects0And6() {
        assertIs<AppResult.Success<ReviewRating>>(ReviewRating.create(1))
        assertIs<AppResult.Success<ReviewRating>>(ReviewRating.create(5))
        assertIs<AppResult.Failure>(ReviewRating.create(0))
        assertIs<AppError.Validation>(ReviewRating.create(6).errorOrNull())
    }

    @Test
    fun createReviewRequest_omitsNullIntentId() {
        val json = createNetworkJson()
        val encoded = json.encodeToString(
            CreateReviewRequestDto.serializer(),
            CreateReviewRequestDto(rating = 5, comment = "Excellent quality", intentId = null),
        )
        assertFalse(encoded.contains("intent_id"))
        val withIntent = json.encodeToString(
            CreateReviewRequestDto.serializer(),
            CreateReviewRequestDto(rating = 5, comment = "Excellent quality", intentId = 1),
        )
        assertTrue(withIntent.contains("intent_id"))
    }

    @Test
    fun listReviews_mapsVerifiedFieldsOnly() = runTest {
        val api = ProductReviewApi(
            client = createEngagementTestClient(
                MockEngine { request ->
                    assertEquals("/api/v1/products/1/reviews", request.url.encodedPath)
                    assertNull(request.headers[HttpHeaders.Authorization])
                    jsonResponse(HttpStatusCode.OK, ReviewsEnvelope)
                },
            ),
            environment = engagementEnvironment,
            executor = createEngagementTestExecutor(),
        )

        val page = DefaultProductReviewRepository(api).getReviews(ProductId(1), CursorPagination())
        val review = requireNotNull(page.getOrNull()).items.single()
        assertEquals(1L, review.id.value)
        assertEquals(2L, review.authorUserId)
        assertEquals(5, review.rating.value)
        assertEquals("Great product", review.comment)
    }

    @Test
    fun submitReview_withoutIntent_andRejectsInvalidRating() = runTest {
        val api = ProductReviewApi(
            client = createEngagementTestClient(
                MockEngine { jsonResponse(HttpStatusCode.Created, SubmittedReviewEnvelope) },
                token = "token",
            ),
            environment = engagementEnvironment,
            executor = createEngagementTestExecutor(),
        )
        val useCase = SubmitProductReviewUseCase(DefaultProductReviewRepository(api))

        assertIs<AppResult.Failure>(useCase(ProductId(1), ratingValue = 0, comment = "x"))
        assertIs<AppResult.Failure>(useCase(ProductId(1), ratingValue = 5, comment = "   "))
        val ok = requireNotNull(useCase(ProductId(1), ratingValue = 5, comment = "Excellent quality").getOrNull())
        assertEquals(1L, ok.id.value)
    }

    @Test
    fun submitReview_withIntentId() = runTest {
        val api = ProductReviewApi(
            client = createEngagementTestClient(
                MockEngine { jsonResponse(HttpStatusCode.Created, SubmittedReviewEnvelope) },
                token = "token",
            ),
            environment = engagementEnvironment,
            executor = createEngagementTestExecutor(),
        )

        val result = SubmitProductReviewUseCase(DefaultProductReviewRepository(api))(
            productId = ProductId(1),
            ratingValue = 5,
            comment = "Excellent quality",
            intentId = PurchaseIntentId(1),
        )

        assertIs<AppResult.Success<*>>(result)
    }

    @Test
    fun publicComments_verifiedFields_andSubmitNotAppended() = runTest {
        val api = ShopCommentApi(
            client = createEngagementTestClient(
                MockEngine { request ->
                    when {
                        request.url.encodedPath.endsWith("/comments") &&
                            request.method.value == "GET" ->
                            jsonResponse(HttpStatusCode.OK, PublicCommentsEnvelope)
                        else -> jsonResponse(HttpStatusCode.Created, SubmittedCommentEnvelope)
                    }
                },
                token = "token",
            ),
            environment = engagementEnvironment,
            executor = createEngagementTestExecutor(),
        )
        val repository = DefaultShopCommentRepository(api)
        val publicPage = requireNotNull(repository.getComments(ShopId(1), CursorPagination()).getOrNull())
        assertEquals(true, publicPage.items.single().confirmed)
        assertEquals("Great shop", publicPage.items.single().title)

        val submitted = requireNotNull(
            SubmitShopCommentUseCase(repository)(
                shopId = ShopId(1),
                title = "Great shop",
                description = "Fast delivery and friendly staff.",
            ).getOrNull(),
        )
        assertEquals(false, submitted.confirmed)
        assertFalse(publicPage.items.any { it.id == submitted.id })
    }

    @Test
    fun contact_anonymousVsAuthenticated_stableSession_whatsappAndUnsupported() = runTest {
        val sessionIds = mutableListOf<String>()
        val authorizations = mutableListOf<String?>()
        val visitor = DefaultVisitorSessionProvider(initialSessionId = "visitor-stable-1")
        val api = ProductContactApi(
            client = createEngagementTestClient(
                MockEngine { request ->
                    sessionIds += request.url.parameters["session_id"].orEmpty()
                    authorizations += request.headers[HttpHeaders.Authorization]
                    val routed = if (sessionIds.size == 3) "webhook" else "whatsapp"
                    jsonResponse(
                        HttpStatusCode.OK,
                        if (routed == "whatsapp") ContactWhatsAppEnvelope else contactUnsupportedEnvelope(routed),
                    )
                },
                token = null,
            ),
            environment = engagementEnvironment,
            executor = createEngagementTestExecutor(),
        )
        val repository = DefaultProductContactRepository(api, visitor)

        val first = requireNotNull(repository.contactProduct(ProductId(1)).getOrNull())
        val second = requireNotNull(repository.contactProduct(ProductId(2)).getOrNull())
        assertIs<ContactRoute.WhatsApp>(first.route)
        assertIs<ContactRoute.WhatsApp>(second.route)
        assertEquals("https://wa.me/989123456789", first.route.url)
        assertEquals("visitor-stable-1", sessionIds[0])
        assertEquals("visitor-stable-1", sessionIds[1])
        assertNull(authorizations[0])
        assertNull(authorizations[1])

        val authedApi = ProductContactApi(
            client = createEngagementTestClient(
                MockEngine { request ->
                    authorizations += request.headers[HttpHeaders.Authorization]
                    jsonResponse(HttpStatusCode.OK, ContactWhatsAppEnvelope)
                },
                token = "token",
            ),
            environment = engagementEnvironment,
            executor = createEngagementTestExecutor(),
        )
        assertTrue(
            DefaultProductContactRepository(authedApi, visitor).contactProduct(ProductId(3)).isSuccess,
        )
        assertEquals("Bearer token", authorizations.last())

        val unsupported = requireNotNull(
            DefaultProductContactRepository(
                ProductContactApi(
                    client = createEngagementTestClient(
                        MockEngine { jsonResponse(HttpStatusCode.OK, contactUnsupportedEnvelope("webhook")) },
                    ),
                    environment = engagementEnvironment,
                    executor = createEngagementTestExecutor(),
                ),
                visitor,
            ).contactProduct(ProductId(4)).getOrNull(),
        )
        assertIs<ContactRoute.Unsupported>(unsupported.route)
        assertEquals("webhook", unsupported.route.rawType)
    }

    @Test
    fun contactUseCase_tracksPurchaseIntentPersonalization() = runTest {
        val tracker = RecordingAnalyticsTracker()
        val useCase = ContactProductUseCase(
            productContactRepository = DefaultProductContactRepository(
                ProductContactApi(
                    client = createEngagementTestClient(
                        MockEngine { jsonResponse(HttpStatusCode.OK, ContactWhatsAppEnvelope) },
                    ),
                    environment = engagementEnvironment,
                    executor = createEngagementTestExecutor(),
                ),
                DefaultVisitorSessionProvider("sid"),
            ),
            analyticsTracker = tracker,
        )

        val result = useCase(ProductId(1))
        assertTrue(result.isSuccess)
        assertEquals(1, tracker.userEvents.size)
    }
}
