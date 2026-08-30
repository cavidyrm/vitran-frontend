package com.vitran.shop.feature.seller.shop.domain.usecase

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.domain.auth.UserRole
import com.vitran.shop.feature.account.domain.model.User
import com.vitran.shop.feature.location.domain.model.CityId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopSlug
import com.vitran.shop.feature.seller.FakeAccountRepository
import com.vitran.shop.feature.seller.FakeSessionRepository
import com.vitran.shop.feature.seller.createSellerRepository
import com.vitran.shop.feature.seller.createShopWithTokenBody
import com.vitran.shop.feature.seller.createShopWithoutTokenBody
import com.vitran.shop.feature.seller.jsonResponse
import com.vitran.shop.feature.seller.shop.domain.model.CreateShopCommand
import com.vitran.shop.feature.seller.shop.domain.model.ShopPublicationState
import io.ktor.client.engine.mock.MockEngine
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant

class CreateShopUseCaseTest {

    @Test
    fun firstShop_updatesAccessToken_preservesRefresh() = runTest {
        val session = FakeSessionRepository()
        val account = FakeAccountRepository(
            refreshResult =
                AppResult.Success(
                    User(
                        id = 1,
                        phone = "0912",
                        username = null,
                        email = null,
                        roles = setOf(UserRole.Customer, UserRole.Seller),
                        verified = true,
                        isActive = true,
                        createdAt = Instant.parse("2026-01-01T00:00:00Z"),
                        updatedAt = Instant.parse("2026-01-01T00:00:00Z"),
                    ),
                ),
        )
        val engine = MockEngine { jsonResponse(HttpStatusCode.Created, createShopWithTokenBody) }
        val (repo, _) = createSellerRepository(engine)
        val useCase = CreateShopUseCase(repo, session, account)

        val result =
            useCase(
                CreateShopCommand(title = "My Shop", type = "retailer", cityId = CityId(1)),
            )

        assertIs<AppResult.Success<*>>(result)
        assertEquals("NEW_ACCESS", session.credentials!!.accessToken)
        assertEquals("REFRESH_1", session.credentials!!.refreshToken)
        assertEquals(Instant.parse("2026-06-09T14:00:00Z"), session.credentials!!.accessTokenExpiresAt)
        assertEquals(1, account.refreshCalls)
        assertEquals(
            ShopPublicationState.PendingApproval,
            (result as AppResult.Success).value.shop.publicationState,
        )
    }

    @Test
    fun roleRefreshFailure_doesNotRollBackCreate_orToken() = runTest {
        val session = FakeSessionRepository()
        val account = FakeAccountRepository(refreshResult = AppResult.Failure(AppError.Network.Timeout()))
        val engine = MockEngine { jsonResponse(HttpStatusCode.Created, createShopWithTokenBody) }
        val (repo, _) = createSellerRepository(engine)
        val useCase = CreateShopUseCase(repo, session, account)

        val result =
            useCase(CreateShopCommand(title = "My Shop", type = "retailer", cityId = CityId(1)))

        assertIs<AppResult.Success<*>>(result)
        assertEquals("NEW_ACCESS", session.credentials!!.accessToken)
        assertEquals("REFRESH_1", session.credentials!!.refreshToken)
    }

    @Test
    fun createWithoutTokens_leavesSessionUnchanged() = runTest {
        val session = FakeSessionRepository()
        val account = FakeAccountRepository()
        val engine = MockEngine { jsonResponse(HttpStatusCode.Created, createShopWithoutTokenBody) }
        val (repo, _) = createSellerRepository(engine)
        val useCase = CreateShopUseCase(repo, session, account)

        val result =
            useCase(CreateShopCommand(title = "Second", type = "retailer", cityId = CityId(1)))

        assertIs<AppResult.Success<*>>(result)
        assertEquals("OLD_ACCESS", session.credentials!!.accessToken)
        assertEquals("REFRESH_1", session.credentials!!.refreshToken)
        assertNull((result as AppResult.Success).value.sessionAccessUpdate)
    }
}
