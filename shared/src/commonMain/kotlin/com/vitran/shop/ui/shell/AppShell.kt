package com.vitran.shop.ui.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.vitran.shop.ui.navigation.AppBottomNav
import com.vitran.shop.ui.navigation.AppSideNav
import com.vitran.shop.ui.navigation.AvatarRenderer
import com.vitran.shop.ui.navigation.DefaultAvatarRenderer
import com.vitran.shop.ui.navigation.NavAuthUiState
import com.vitran.shop.ui.navigation.Route
import com.vitran.shop.ui.theme.VitranSize

@Composable
fun AppShell(
    currentRoute: Route,
    authState: NavAuthUiState,
    onNavigate: (Route) -> Unit,
    onLoginRequest: () -> Unit,
    avatarRenderer: AvatarRenderer = DefaultAvatarRenderer,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .then(modifier),
    ) {
        val isDesktop = maxWidth >= VitranSize.desktopBreakpoint
        CompositionLocalProvider(LocalDesktopLayout provides isDesktop) {
            if (isDesktop) {
                // In RTL, Row Start is on the right — put nav first so the rail sits at Start.
                Row(modifier = Modifier.fillMaxSize()) {
                    AppSideNav(
                        currentRoute = currentRoute,
                        authState = authState,
                        onNavigate = onNavigate,
                        onLoginRequest = onLoginRequest,
                        avatarRenderer = avatarRenderer,
                        modifier = Modifier.fillMaxHeight(),
                    )
                    AppContentContainer(
                        framed = true,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                    ) {
                        content()
                    }
                }
            } else {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = {
                        AppBottomNav(
                            currentRoute = currentRoute,
                            authState = authState,
                            onNavigate = onNavigate,
                            onLoginRequest = onLoginRequest,
                            avatarRenderer = avatarRenderer,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    },
                ) { innerPadding ->
                    AppContentContainer(
                        framed = false,
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                    ) {
                        content()
                    }
                }
            }
        }
    }
}
