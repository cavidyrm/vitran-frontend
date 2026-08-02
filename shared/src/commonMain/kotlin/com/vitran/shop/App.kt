package com.vitran.shop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.vitran.shop.ui.navigation.AppDestination
import com.vitran.shop.ui.navigation.NavAuthUiState
import com.vitran.shop.ui.screens.AccountScreen
import com.vitran.shop.ui.screens.CategoriesScreen
import com.vitran.shop.ui.screens.HomeScreen
import com.vitran.shop.ui.screens.OffersScreen
import com.vitran.shop.ui.screens.SavedScreen
import com.vitran.shop.ui.shell.AppShell
import com.vitran.shop.ui.theme.VitranTheme

@Composable
@Preview
fun App() {
    VitranTheme {
        var selected by remember { mutableStateOf<AppDestination>(AppDestination.Home) }
        // Mock auth for UI phase — swap to SignedIn(avatarUrl = null) to preview avatar.
        val authState: NavAuthUiState = NavAuthUiState.SignedOut

        AppShell(
            selected = selected,
            authState = authState,
            onNavigate = { destination -> selected = destination },
            onLoginRequest = {
                // Login UI is out of scope for this section.
            },
        ) {
            when (selected) {
                is AppDestination.Home -> HomeScreen()
                is AppDestination.Categories -> CategoriesScreen()
                is AppDestination.Offers -> OffersScreen()
                is AppDestination.Saved -> SavedScreen()
                is AppDestination.Account -> AccountScreen()
            }
        }
    }
}
