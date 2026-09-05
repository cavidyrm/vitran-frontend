package com.vitran.shop.ui.navigation

import com.vitran.shop.core.session.domain.SessionState
import com.vitran.shop.feature.account.domain.model.CurrentUserState
import com.vitran.shop.feature.account.domain.model.User
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class NavAuthUiStateTest {

    @Test
    fun restoring_isNotSignInChrome() {
        val state = navAuthUiStateOf(SessionState.Restoring, CurrentUserState.Unknown)
        assertEquals(NavAuthUiState.Restoring, state)
        assertIs<NavAuthUiState.Restoring>(state)
    }

    @Test
    fun anonymous_isSignedOut() {
        assertEquals(
            NavAuthUiState.SignedOut,
            navAuthUiStateOf(SessionState.Anonymous, CurrentUserState.Unknown),
        )
    }

    @Test
    fun authenticated_isSignedIn_evenIfUserNotLoaded() {
        val state = navAuthUiStateOf(SessionState.Authenticated, CurrentUserState.Unknown)
        assertIs<NavAuthUiState.SignedIn>(state)
        assertNull(state.avatarUrl)
    }

    @Test
    fun authenticated_isSignedIn_whileUserLoading() {
        val state = navAuthUiStateOf(SessionState.Authenticated, CurrentUserState.Loading)
        assertEquals(NavAuthUiState.SignedIn(avatarUrl = null), state)
    }

    @Test
    fun authenticated_usesUsernameWhenAvailable() {
        val state = navAuthUiStateOf(
            SessionState.Authenticated,
            CurrentUserState.Available(sampleUser(username = "علی")),
        )
        assertEquals(NavAuthUiState.SignedIn(avatarUrl = "علی"), state)
    }

    private fun sampleUser(username: String?) = User(
        id = 1,
        phone = "09120000000",
        username = username,
        email = null,
        roles = emptySet(),
        verified = true,
        isActive = true,
        createdAt = Instant.parse("2026-01-01T00:00:00Z"),
        updatedAt = Instant.parse("2026-01-01T00:00:00Z"),
    )
}
