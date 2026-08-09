package com.vitran.shop

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import com.vitran.shop.ui.navigation.AppNavHost
import com.vitran.shop.ui.navigation.BindBrowserNavigation
import com.vitran.shop.ui.navigation.NavAuthUiState
import com.vitran.shop.ui.navigation.Route
import com.vitran.shop.ui.navigation.initWebComposeResources
import com.vitran.shop.ui.navigation.rememberInitialRoute
import com.vitran.shop.ui.navigation.rememberNavigationState
import com.vitran.shop.ui.navigation.rememberNavigator
import com.vitran.shop.ui.shell.AppShell
import com.vitran.shop.ui.theme.VitranTheme

@Composable
@Preview
fun App() {
    // Once per process: absolute composeResources URLs on web (see expect actual).
    initWebComposeResources()

    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context).build()
    }

    VitranTheme {
        val startRoute = rememberInitialRoute()
        val navState = rememberNavigationState(start = startRoute)
        val navigator = rememberNavigator(navState)
        BindBrowserNavigation(navState = navState, navigator = navigator)

        // Mock auth for UI phase — swap to SignedIn(avatarUrl = null) to preview avatar.
        val authState: NavAuthUiState = NavAuthUiState.SignedOut

        AppShell(
            currentRoute = navState.chromeRoute,
            authState = authState,
            onNavigate = navigator::navigate,
            onLoginRequest = { navigator.push(Route.Login) },
            hideChrome = navState.currentRoute is Route.Login,
        ) {
            AppNavHost(navState = navState, navigator = navigator)
        }
    }
}
