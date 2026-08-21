package com.vitran.shop.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.vitran.shop.ui.components.DownloadAppBanner
import com.vitran.shop.ui.components.FloatingSearchFab
import com.vitran.shop.ui.components.FloatingSearchOmnibox
import com.vitran.shop.ui.components.OmniboxMobileSearchSheet
import com.vitran.shop.ui.components.OmniboxResult
import com.vitran.shop.ui.components.SiteFooter
import com.vitran.shop.ui.components.SiteFooterLinkId
import com.vitran.shop.ui.sections.home.HomeCategoriesRow
import com.vitran.shop.ui.sections.home.HomeCategoryMosaicsRow
import com.vitran.shop.ui.sections.home.HomeCategoryShopsFeed
import com.vitran.shop.ui.sections.home.HomeHero
import com.vitran.shop.ui.sections.home.rememberMockHomeCategories
import com.vitran.shop.ui.sections.home.rememberMockHomeCategoryMosaics
import com.vitran.shop.ui.sections.home.rememberMockHomeCategoryShopSections
import com.vitran.shop.ui.shell.LocalDesktopLayout
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

/** shop.app mobile overlay `backdrop-blur-[10px]` — blur content under the sheet. */
private val MobileOmniboxBackdropBlur = 10.dp

/**
 * Home screen host. Sections are added one at a time.
 *
 * @param onProductOpen product id, title, image URL, store name, and price from a product click.
 * @param onStoreOpen store / merchant id from a shop card click.
 */
@Composable
fun HomeScreen(
    onProductOpen: (
        id: String,
        title: String,
        imageUrl: String,
        storeName: String,
        priceLabel: String,
    ) -> Unit = { _, _, _, _, _ -> },
    onStoreOpen: (shopId: String) -> Unit = {},
    onFooterLinkClick: (SiteFooterLinkId) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val isDesktop = LocalDesktopLayout.current
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    var query by remember { mutableStateOf("") }
    var omniboxExpanded by remember { mutableStateOf(false) }
    var omniboxBoundsInRoot by remember { mutableStateOf(Rect.Zero) }
    var screenOriginInRoot by remember { mutableStateOf(Offset.Zero) }
    // Live layout coords for the collapsed hero field. Re-read boundsInRoot whenever
    // scroll changes — onGloballyPositioned alone does not recompose every scroll frame.
    var omniboxCollapsedCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    // Fallback: bottom of field in scroll-content space (stable if live bounds stall).
    var omniboxBottomInContentPx by remember { mutableFloatStateOf(Float.NaN) }

    val heroOmniboxOffScreen by remember {
        derivedStateOf {
            val scroll = scrollState.value
            val coords = omniboxCollapsedCoords
            if (coords != null && coords.isAttached) {
                val liveBottom = coords.boundsInRoot().bottom
                if (liveBottom <= screenOriginInRoot.y) return@derivedStateOf true
            }
            val contentBottom = omniboxBottomInContentPx
            if (!contentBottom.isNaN()) {
                return@derivedStateOf contentBottom - scroll <= 0f
            }
            false
        }
    }
    val showFloatingSearch = heroOmniboxOffScreen && !(!isDesktop && omniboxExpanded)

    // When floating chrome owns search, keep the hero field collapsed (no off-screen Popup).
    val heroOmniboxExpanded = omniboxExpanded && !(isDesktop && showFloatingSearch)
    val floatingOmniboxExpanded = omniboxExpanded && isDesktop && showFloatingSearch

    fun dismissOmnibox() {
        query = ""
        omniboxExpanded = false
        focusManager.clearFocus()
    }

    val expandedState = rememberUpdatedState(omniboxExpanded)
    val boundsState = rememberUpdatedState(omniboxBoundsInRoot)
    val originState = rememberUpdatedState(screenOriginInRoot)
    val desktopState = rememberUpdatedState(isDesktop)
    val onDismissOmnibox by rememberUpdatedState(newValue = { dismissOmnibox() })

    // Desktop: scrolling the Home page (outside the typeahead) dismisses search.
    LaunchedEffect(isDesktop, omniboxExpanded, scrollState) {
        if (!isDesktop || !omniboxExpanded) return@LaunchedEffect
        snapshotFlow { scrollState.isScrollInProgress }
            .distinctUntilChanged()
            .filter { scrolling -> scrolling }
            .collect { onDismissOmnibox() }
    }

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
                    // Compact sheet locks the page; desktop keeps page scroll so the wheel
                    // outside the non-focusable omnibox Popup still moves Home.
                    enabled = isDesktop || !omniboxExpanded,
                ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    // Desktop typeahead overlays following scroll siblings (categories, etc.).
                    .zIndex(if (isDesktop && heroOmniboxExpanded) 20f else 0f),
            ) {
                HomeHero(
                    query = query,
                    onQueryChange = { query = it },
                    omniboxExpanded = heroOmniboxExpanded,
                    onOmniboxExpandedChange = { omniboxExpanded = it },
                    onOmniboxBoundsInRoot = { bounds ->
                        if (!(isDesktop && showFloatingSearch)) {
                            omniboxBoundsInRoot = bounds
                        }
                    },
                    onOmniboxCollapsedLayoutCoordinates = { coords ->
                        omniboxCollapsedCoords = coords
                        // Snapshot content-space Y only when not mid-scroll to avoid
                        // double-counting if bounds omit the scroll offset.
                        if (!scrollState.isScrollInProgress) {
                            omniboxBottomInContentPx =
                                coords.boundsInRoot().bottom -
                                    screenOriginInRoot.y +
                                    scrollState.value
                        }
                    },
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
            HomeCategoryMosaicsRow(
                mosaics = rememberMockHomeCategoryMosaics(),
                onCategoryClick = { /* mock — category landing not wired yet */ },
                onTileClick = { _, _ -> /* mock — subcategory landing not wired yet */ },
                modifier = Modifier.fillMaxWidth(),
            )
            HomeCategoryShopsFeed(
                sections = rememberMockHomeCategoryShopSections(),
                onCategoryClick = { /* mock — category landing not wired yet */ },
                onShopClick = { _, shop -> onStoreOpen(shop.id) },
                onProductClick = { _, shop, peek ->
                    onProductOpen(
                        peek.id,
                        peek.title,
                        peek.imageUrl,
                        shop.name,
                        peek.priceLabel,
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            )
            SiteFooter(
                onLinkClick = onFooterLinkClick,
                onLanguageClick = { /* mock — language settings not wired yet */ },
                onDownloadClick = { /* mock — store deep link not wired yet */ },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // Desktop: floating pill when hero omnibox scrolls off-screen.
        if (isDesktop) {
            FloatingSearchOmnibox(
                visible = showFloatingSearch,
                query = query,
                onQueryChange = { query = it },
                expanded = floatingOmniboxExpanded,
                onExpandedChange = { omniboxExpanded = it },
                onSubmit = { /* mock — search screen not wired yet */ },
                onDismiss = { dismissOmnibox() },
                onBoundsInRoot = { omniboxBoundsInRoot = it },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .zIndex(30f),
            )
        } else {
            // Compact: FAB above bottom nav (host already clears nav); tap opens sheet.
            FloatingSearchFab(
                visible = showFloatingSearch,
                onClick = { omniboxExpanded = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .zIndex(100f),
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
