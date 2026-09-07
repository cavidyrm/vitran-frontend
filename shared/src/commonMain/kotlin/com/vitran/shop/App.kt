package com.vitran.shop

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import com.vitran.shop.core.session.domain.SessionState
import com.vitran.shop.core.session.repository.SessionRepository
import com.vitran.shop.di.AppSessionCoordinator
import com.vitran.shop.di.startVitranKoin
import com.vitran.shop.feature.account.domain.repository.AccountRepository
import com.vitran.shop.ui.navigation.AppNavHost
import com.vitran.shop.ui.navigation.BindBrowserNavigation
import com.vitran.shop.ui.navigation.Route
import com.vitran.shop.ui.navigation.hidesChrome
import com.vitran.shop.ui.navigation.initWebComposeResources
import com.vitran.shop.ui.navigation.navAuthUiStateOf
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

    val sessionRepository: SessionRepository = koinInject()
    val sessionState by sessionRepository.sessionState.collectAsState()
    val accountRepository: AccountRepository = koinInject()
    val currentUser by accountRepository.currentUserState.collectAsState()

    VitranTheme {
        val startRoute = rememberInitialRoute()
        val navState = rememberNavigationState(start = startRoute)
        val navigator = rememberNavigator(navState)
        BindBrowserNavigation(navState = navState, navigator = navigator)

        val authState = navAuthUiStateOf(sessionState, currentUser)

        AppShell(
            currentRoute = navState.chromeRoute,
            authState = authState,
            onNavigate = navigator::navigate,
            onLoginRequest = { navigator.push(Route.Login) },
            hideChrome = navState.currentRoute.hidesChrome(),
        ) {
            if (sessionState == SessionState.Restoring) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else {
                AppNavHost(navState = navState, navigator = navigator)
            }
        }
    }
}
