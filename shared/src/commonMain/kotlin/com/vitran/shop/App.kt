package com.vitran.shop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import com.vitran.shop.core.session.domain.SessionState
import com.vitran.shop.di.AppSessionCoordinator
import com.vitran.shop.di.startVitranKoin
import com.vitran.shop.feature.account.domain.model.CurrentUserState
import com.vitran.shop.feature.account.domain.repository.AccountRepository
import com.vitran.shop.ui.navigation.AppNavHost
import com.vitran.shop.ui.navigation.BindBrowserNavigation
import com.vitran.shop.ui.navigation.NavAuthUiState
import com.vitran.shop.ui.navigation.Route
import com.vitran.shop.ui.navigation.hidesChrome
import com.vitran.shop.ui.navigation.initWebComposeResources
import com.vitran.shop.ui.navigation.rememberInitialRoute
import com.vitran.shop.ui.navigation.rememberNavigationState
import com.vitran.shop.ui.navigation.rememberNavigator
import com.vitran.shop.ui.shell.AppShell
import com.vitran.shop.ui.theme.VitranTheme
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    startVitranKoin()

    initWebComposeResources()

    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context).build()
    }

    val appSessionCoordinator: AppSessionCoordinator = koinInject()
    LaunchedEffect(appSessionCoordinator) {
        appSessionCoordinator.start()
    }

    val sessionState by appSessionCoordinator.sessionState.collectAsStateWithLifecycle()
    val accountRepository: AccountRepository = koinInject()
    val currentUser by accountRepository.currentUserState.collectAsStateWithLifecycle()

    VitranTheme {
        val startRoute = rememberInitialRoute()
        val navState = rememberNavigationState(start = startRoute)
        val navigator = rememberNavigator(navState)
        BindBrowserNavigation(navState = navState, navigator = navigator)

        val authState = when (sessionState) {
            SessionState.Restoring -> NavAuthUiState.SignedOut
            SessionState.Anonymous -> NavAuthUiState.SignedOut
            SessionState.Authenticated -> {
                val avatar = (currentUser as? CurrentUserState.Available)?.user?.username
                NavAuthUiState.SignedIn(avatarUrl = avatar)
            }
        }

        AppShell(
            currentRoute = navState.chromeRoute,
            authState = authState,
            onNavigate = navigator::navigate,
            onLoginRequest = { navigator.push(Route.Login) },
            hideChrome = navState.currentRoute.hidesChrome(),
        ) {
            AppNavHost(navState = navState, navigator = navigator)
        }
    }
}
