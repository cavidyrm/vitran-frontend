package com.vitran.shop.ui.navigation

import androidx.compose.runtime.Composable

@Composable
actual fun rememberInitialRoute(fallback: Route): Route = fallback

@Composable
actual fun BindBrowserNavigation(
    navState: NavigationState,
    navigator: Navigator,
) {
    // No browser History on Desktop JVM.
}
