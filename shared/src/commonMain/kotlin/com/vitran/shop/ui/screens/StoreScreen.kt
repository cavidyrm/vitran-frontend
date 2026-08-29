package com.vitran.shop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.vitran.shop.ui.components.FloatingSearchFab
import com.vitran.shop.ui.components.FloatingSearchOmnibox
import com.vitran.shop.ui.components.OmniboxMobileSearchSheet
import com.vitran.shop.ui.components.OmniboxResult
import com.vitran.shop.ui.components.SiteFooter
import com.vitran.shop.ui.components.SiteFooterLinkId
import com.vitran.shop.ui.sections.product.ProductDetailReviewsSheet
import com.vitran.shop.ui.sections.store.StoreCategoryChipsBar
import com.vitran.shop.ui.sections.store.StoreCollectionsSection
import com.vitran.shop.ui.sections.store.StoreCoverLayer
import com.vitran.shop.ui.sections.store.StoreIdentityBlock
import com.vitran.shop.ui.sections.store.StoreMenuFollowBar
import com.vitran.shop.ui.sections.store.StoreMenuSheet
import com.vitran.shop.ui.sections.store.StoreProductsSection
import com.vitran.shop.ui.sections.store.rememberMockStore
import com.vitran.shop.ui.sections.store.rememberMockStoreMenu
import com.vitran.shop.ui.sections.store.rememberMockStoreProducts
import com.vitran.shop.ui.sections.store.rememberStoreCoverHeight
import com.vitran.shop.ui.shell.LocalDesktopLayout
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
/** shop.app mobile overlay `backdrop-blur-[10px]` — blur content under the sheet. */
private val MobileOmniboxBackdropBlur = 10.dp

private const val StoreIdentityKey = "store-identity"
private const val StoreChipsKey = "store-chips"

/**
 * Store / merchant page (shop.app `/m/{shopId}`).
 *
 * Scroll chrome mirrors shop.app `store-screen-v2`:
 * - Cover media sticks behind content and collapses / fades while scrolling
 * - Menu / Follow + category chips use [LazyColumn] sticky headers
 * - Wordmark / rating scroll away; collections + products follow
 *
 * Floating search always on (same as Categories) — desktop bottom omnibox /
 * compact FAB; FAB hides while the mobile sheet is open.
 * [SiteFooter] owns its own top pad (48 compact / 136 desktop).
 */
@Composable
fun StoreScreen(
    shopId: String,
    modifier: Modifier = Modifier,
    onProductOpen: (
        productId: String,
        title: String,
        imageUrl: String,
        storeName: String,
        priceLabel: String,
    ) -> Unit = { _, _, _, _, _ -> },
    onFooterLinkClick: (SiteFooterLinkId) -> Unit = {},
) {
    val store = rememberMockStore(shopId)
    val productsMock = rememberMockStoreProducts(store.name)
    val menuMock = rememberMockStoreMenu(store)
    val isDesktop = LocalDesktopLayout.current
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()
    val coverHeight = rememberStoreCoverHeight()
    val density = LocalDensity.current
    val coverHeightPx = with(density) { coverHeight.toPx() }

    var query by remember { mutableStateOf("") }
    var omniboxExpanded by remember { mutableStateOf(false) }
    var omniboxBoundsInRoot by remember { mutableStateOf(Rect.Zero) }
    var screenOriginInRoot by remember { mutableStateOf(Offset.Zero) }
    var menuOpen by remember { mutableStateOf(false) }
    var storeReviewsOpen by remember { mutableStateOf(false) }

    // Always visible on Store; hide compact FAB only while the sheet is open.
    val showFloatingSearch = isDesktop || !omniboxExpanded

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

    // Scroll-linked collapse (shop.app sticky cover + content sliding over it).
    val collapseProgress by remember(listState, coverHeightPx) {
        derivedStateOf {
            storeCoverCollapseProgress(
                listState = listState,
                coverHeightPx = coverHeightPx,
            )
        }
    }
    // Desktop: scrolling the page (outside the typeahead) dismisses search.
    LaunchedEffect(isDesktop, omniboxExpanded, listState) {
        if (!isDesktop || !omniboxExpanded) return@LaunchedEffect
        snapshotFlow { listState.isScrollInProgress }
            .distinctUntilChanged()
            .filter { scrolling -> scrolling }
            .collect { onDismissOmnibox() }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(store.brandColor)
            .onGloballyPositioned { coords ->
                screenOriginInRoot = coords.positionInRoot()
            }
            .then(
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
        // Cover sits under the list (shop.app sticky media + negative margin).
        StoreCoverLayer(
            store = store,
            coverHeight = coverHeight,
            collapseProgress = collapseProgress,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(0f),
        )

        LazyColumn(
            state = listState,
            userScrollEnabled = isDesktop || !omniboxExpanded,
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f)
                .then(
                    if (!isDesktop && omniboxExpanded) {
                        Modifier.blur(MobileOmniboxBackdropBlur)
                    } else {
                        Modifier
                    },
                ),
        ) {
            stickyHeader(key = "store-menu") {
                StoreMenuFollowBar(
                    store = store,
                    onMenuClick = { menuOpen = true },
                    onFollowClick = { /* mock — follow not wired yet */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(store.brandColor.copy(alpha = collapseProgress)),
                )
            }

            item(key = StoreIdentityKey) {
                StoreIdentityBlock(
                    store = store,
                    coverHeight = coverHeight,
                    onRatingClick = { storeReviewsOpen = true },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            stickyHeader(key = StoreChipsKey) {
                StoreCategoryChipsBar(
                    store = store,
                    onNavChipClick = { /* mock — collection filter not wired yet */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(store.brandColor.copy(alpha = collapseProgress)),
                )
            }

            item(key = "store-collections") {
                StoreCollectionsSection(
                    store = store,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            item(key = "store-products") {
                StoreProductsSection(
                    storeName = store.name,
                    brandColor = store.brandColor,
                    products = productsMock.products,
                    onProductClick = { product ->
                        onProductOpen(
                            product.id,
                            product.title,
                            product.imageUrl,
                            product.storeName,
                            product.priceLabel,
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            item(key = "store-footer") {
                SiteFooter(
                    onLinkClick = onFooterLinkClick,
                    onLanguageClick = { /* mock — language settings not wired yet */ },
                    onDownloadClick = { /* mock — store deep link not wired yet */ },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        if (isDesktop) {
            FloatingSearchOmnibox(
                visible = showFloatingSearch,
                query = query,
                onQueryChange = { query = it },
                expanded = omniboxExpanded,
                onExpandedChange = { omniboxExpanded = it },
                onSubmit = { /* mock — search screen not wired yet */ },
                onDismiss = { dismissOmnibox() },
                onBoundsInRoot = { omniboxBoundsInRoot = it },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .zIndex(30f),
            )
        } else {
            FloatingSearchFab(
                visible = showFloatingSearch,
                onClick = { omniboxExpanded = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .zIndex(100f),
            )
        }

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

        if (menuOpen) {
            StoreMenuSheet(
                store = store,
                menu = menuMock,
                onDismiss = {
                    menuOpen = false
                    storeReviewsOpen = false
                },
                onOpenReviews = { storeReviewsOpen = true },
            )
        }

        if (storeReviewsOpen) {
            ProductDetailReviewsSheet(
                reviews = menuMock.fullReviews,
                onDismiss = { storeReviewsOpen = false },
            )
        }
    }
}

/**
 * Maps LazyColumn layout to a 0→1 cover collapse.
 *
 * Uses the identity block’s bottom edge vs cover height so the media shrinks
 * smoothly as wordmark/rating scroll away and chips approach the top.
 */
private fun storeCoverCollapseProgress(
    listState: LazyListState,
    coverHeightPx: Float,
): Float {
    if (coverHeightPx <= 0f) return 0f
    val info = listState.layoutInfo
    val identity = info.visibleItemsInfo.find { it.key == StoreIdentityKey }
    if (identity != null) {
        val bottom = (identity.offset + identity.size).toFloat()
        return (1f - bottom / coverHeightPx).coerceIn(0f, 1f)
    }
    // Identity off-screen above → fully collapsed once chips (or below) own the top.
    return if (listState.firstVisibleItemIndex >= 1) 1f else 0f
}
