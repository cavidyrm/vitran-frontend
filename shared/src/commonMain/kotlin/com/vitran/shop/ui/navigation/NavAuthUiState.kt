package com.vitran.shop.ui.navigation

import com.vitran.shop.core.session.domain.SessionState
import com.vitran.shop.feature.account.domain.model.CurrentUserState

sealed interface NavAuthUiState {
    /** Session restore in progress — chrome must not show sign-in. */
    data object Restoring : NavAuthUiState
    data object SignedOut : NavAuthUiState
    data class SignedIn(val avatarUrl: String?) : NavAuthUiState
}

fun navAuthUiStateOf(
    sessionState: SessionState,
    currentUser: CurrentUserState,
): NavAuthUiState = when (sessionState) {
    SessionState.Restoring -> NavAuthUiState.Restoring
    SessionState.Anonymous -> NavAuthUiState.SignedOut
    SessionState.Authenticated -> {
        val avatar = (currentUser as? CurrentUserState.Available)?.user?.username
        NavAuthUiState.SignedIn(avatarUrl = avatar)
    }
}
