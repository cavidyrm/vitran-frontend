package com.vitran.shop.ui.navigation

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Picks side rail vs bottom bar. Prefer [com.vitran.shop.ui.shell.AppShell] for app layout —
 * it owns the desktop breakpoint and [androidx.compose.material3.Scaffold] padding.
 */
@Composable
fun AppNavigation(
    currentRoute: Route,
    authState: NavAuthUiState,
    onNavigate: (Route) -> Unit,
    onLoginRequest: () -> Unit,
    isDesktop: Boolean,
    avatarRenderer: AvatarRenderer = DefaultAvatarRenderer,
    modifier: Modifier = Modifier,
) {
    if (isDesktop) {
        AppSideNav(
            currentRoute = currentRoute,
            authState = authState,
            onNavigate = onNavigate,
            onLoginRequest = onLoginRequest,
            avatarRenderer = avatarRenderer,
            modifier = modifier.fillMaxHeight(),
        )
    } else {
        AppBottomNav(
            currentRoute = currentRoute,
            authState = authState,
            onNavigate = onNavigate,
            onLoginRequest = onLoginRequest,
            avatarRenderer = avatarRenderer,
            modifier = modifier.fillMaxWidth(),
        )
    }
}
