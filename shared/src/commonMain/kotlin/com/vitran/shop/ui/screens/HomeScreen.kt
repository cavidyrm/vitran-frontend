package com.vitran.shop.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.vitran.shop.ui.components.DownloadAppBanner
import com.vitran.shop.ui.components.OmniboxMobileSearchSheet
import com.vitran.shop.ui.components.OmniboxResult
import com.vitran.shop.ui.sections.home.HomeCategoriesRow
import com.vitran.shop.ui.sections.home.HomeHero
import com.vitran.shop.ui.sections.home.rememberMockHomeCategories
import com.vitran.shop.ui.shell.LocalDesktopLayout

/** shop.app mobile overlay `backdrop-blur-[10px]` — blur content under the sheet. */
private val MobileOmniboxBackdropBlur = 10.dp

/**
 * Home screen host. Sections are added one at a time.
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
) {
    val isDesktop = LocalDesktopLayout.current
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    var query by remember { mutableStateOf("") }
    var omniboxExpanded by remember { mutableStateOf(false) }
    var omniboxBoundsInRoot by remember { mutableStateOf(Rect.Zero) }
    var screenOriginInRoot by remember { mutableStateOf(Offset.Zero) }

    fun dismissOmnibox() {
        query = ""
        omniboxExpanded = false
        focusManager.clearFocus()
    }

    val expandedState = rememberUpdatedState(omniboxExpanded)
    val boundsState = rememberUpdatedState(omniboxBoundsInRoot)
    val originState = rememberUpdatedState(screenOriginInRoot)
    val desktopState = rememberUpdatedState(isDesktop)

    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { coords ->
                screenOriginInRoot = coords.positionInRoot()
            }
            .then(
                // Outside-tap dismiss is desktop-only; compact uses the mobile sheet X.
                if (isDesktop) {
                    Modifier.pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent(PointerEventPass.Initial)
                                if (!desktopState.value || !expandedState.value) continue
                                val change = event.changes.firstOrNull() ?: continue
                                val isDown = change.pressed && !change.previousPressed
                                if (!isDown) continue
                                val rootPos = change.position + originState.value
                                if (!boundsState.value.contains(rootPos)) {
                                    dismissOmnibox()
                                }
                            }
                        }
                    }
                } else {
                    Modifier
                },
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    // Equivalent to shop.app `backdrop-blur-[10px]` on the frosted overlay:
                    // blur the home content so it shows through the translucent sheet.
                    if (!isDesktop && omniboxExpanded) {
                        Modifier.blur(MobileOmniboxBackdropBlur)
                    } else {
                        Modifier
                    },
                )
                .verticalScroll(
                    state = scrollState,
                    enabled = !omniboxExpanded,
                ),
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                HomeHero(
                    query = query,
                    onQueryChange = { query = it },
                    omniboxExpanded = omniboxExpanded,
                    onOmniboxExpandedChange = { omniboxExpanded = it },
                    onOmniboxBoundsInRoot = { omniboxBoundsInRoot = it },
                    onOmniboxDismiss = { dismissOmnibox() },
                    modifier = Modifier.fillMaxWidth(),
                )
                DownloadAppBanner(
                    onClick = {
                        if (omniboxExpanded) {
                            dismissOmnibox()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .zIndex(10f),
                )
            }
            HomeCategoriesRow(
                categories = rememberMockHomeCategories(),
                onCategoryClick = { /* mock — category landing not wired yet */ },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // shop.app compact: sheet fills content area above bottom nav (outside scroll).
        if (!isDesktop && omniboxExpanded) {
            OmniboxMobileSearchSheet(
                query = query,
                onQueryChange = { query = it },
                onSubmit = { /* mock — search screen not wired yet */ },
                onDismiss = { dismissOmnibox() },
                onResultClick = { result ->
                    query = when (result) {
                        is OmniboxResult.Shop -> result.name
                        is OmniboxResult.Keyword -> result.fullText
                    }
                    dismissOmnibox()
                },
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(30f),
            )
        }
    }
}
