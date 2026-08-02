package com.vitran.shop.ui.navigation

sealed interface NavAuthUiState {
    data object SignedOut : NavAuthUiState
    data class SignedIn(val avatarUrl: String?) : NavAuthUiState
}
