package com.vitran.shop.ui.shell

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier

/**
 * Marker local so children know they are inside the compact content host
 * (above [com.vitran.shop.ui.navigation.AppBottomNav]).
 *
 * Mobile omnibox sheet is composed by [com.vitran.shop.ui.screens.HomeScreen]
 * as a sibling above the scroll column so it fills this host without covering nav.
 */
val LocalOmniboxOverlayHostActive = staticCompositionLocalOf { false }

/**
 * Wraps compact [AppShell] page content. Sheet overlays should fill this box
 * so [com.vitran.shop.ui.navigation.AppBottomNav] stays visible (shop.app).
 */
@Composable
fun OmniboxOverlayHost(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalOmniboxOverlayHostActive provides true) {
        Box(modifier = modifier.fillMaxSize()) {
            content()
        }
    }
}
