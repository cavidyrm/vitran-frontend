package com.vitran.shop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.vitran.shop.ui.components.FloatingSearchFab
import com.vitran.shop.ui.components.FloatingSearchOmnibox
import com.vitran.shop.ui.components.OmniboxMobileSearchSheet
import com.vitran.shop.ui.components.OmniboxResult
import com.vitran.shop.ui.components.SiteFooter
import com.vitran.shop.ui.sections.categories.CategoriesSectionGap
import com.vitran.shop.ui.sections.home.allMockHomeShopCards
import com.vitran.shop.ui.sections.product.MockProductCatalog
import com.vitran.shop.ui.sections.product.ProductDetailInfoColumn
import com.vitran.shop.ui.sections.product.ProductDetailMediaSection
import com.vitran.shop.ui.sections.product.ProductDetailMerchant
import com.vitran.shop.ui.sections.product.ProductDetailMerchantHeader
import com.vitran.shop.ui.sections.product.ProductDetailRecommendationsSection
import com.vitran.shop.ui.sections.product.productDetailStickyGallery
import com.vitran.shop.ui.shell.LocalDesktopLayout
import com.vitran.shop.ui.shell.LocalShellViewportWidth
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

/** shop.app mobile overlay `backdrop-blur-[10px]` — blur content under the sheet. */
private val MobileOmniboxBackdropBlur = 10.dp

/**
 * Product detail (shop.app `/products/{id}/{slug}`).
 *
 * Layout (measured on live shop.app LTR, mirrored via [VitranTheme] RTL):
 * - Compact (`< md`): merchant (+ Visit store) → media → info
 * - Mid+ (`≥ md`): one row — gallery `weight(1)` | buy `md:w-[29em]`; gallery
 *   sticks (`md:sticky md:top-space-32`) while the buy column scrolls through
 *   Follow, then the whole media|info row scrolls away before recommendations.
 *   RTL: Row Start is on the right, so `[gallery][info]` paints as info | gallery.
 * - Large (`≥ lg`): merchant moves into the buy column (no Visit store)
 *
 * Page pad `md:px-space-16`, row `md:mt-space-24` + `md:gap-space-40`.
 * Below info: recommendations (More from / Related / Discover) + [SiteFooter].
 * Floating search always on (same as Categories) — desktop bottom omnibox /
 * compact FAB; FAB hides while the mobile sheet is open.
 * No Add to cart / Buy now (mock phase — no purchase flow).
 *
 * @param onProductOpen product id, title, image URL, store name, and price from a recommendation click.
 * @param onStoreOpen store / merchant id from Visit store (when [ProductDetailMerchant.shopId] is set).
 */
@Composable
fun ProductDetailScreen(
    productId: String,
    modifier: Modifier = Modifier,
    onProductOpen: (
        id: String,
        title: String,
        imageUrl: String,
        storeName: String,
        priceLabel: String,
    ) -> Unit = { _, _, _, _, _ -> },
    onStoreOpen: (shopId: String) -> Unit = {},
) {
    val product = MockProductCatalog.byId(productId)
    val viewportWidth = LocalShellViewportWidth.current
    val isMdUp = viewportWidth >= VitranSize.mdBreakpoint
    val isLgUp = viewportWidth >= VitranSize.desktopBreakpoint
    val isDesktop = LocalDesktopLayout.current
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()

    var query by remember { mutableStateOf("") }
    var omniboxExpanded by remember { mutableStateOf(false) }
    var omniboxBoundsInRoot by remember { mutableStateOf(Rect.Zero) }
    var screenOriginInRoot by remember { mutableStateOf(Offset.Zero) }

    // Always visible on PDP; hide compact FAB only while the sheet is open.
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

    // Desktop: scrolling the page (outside the typeahead) dismisses search.
    LaunchedEffect(isDesktop, omniboxExpanded, listState) {
        if (!isDesktop || !omniboxExpanded) return@LaunchedEffect
        snapshotFlow { listState.isScrollInProgress }
            .distinctUntilChanged()
            .filter { scrolling -> scrolling }
            .collect { onDismissOmnibox() }
    }

    if (product == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.TopStart,
        ) {
            Text(
                text = "محصول پیدا نشد",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(VitranSpacing.lg),
            )
        }
        return
    }

    Box(
        modifier = modifier
            .fillMaxSize()
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
        LazyColumn(
            state = listState,
            userScrollEnabled = isDesktop || !omniboxExpanded,
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (!isDesktop && omniboxExpanded) {
                        Modifier.blur(MobileOmniboxBackdropBlur)
                    } else {
                        Modifier
                    },
                ),
            contentPadding = PaddingValues(bottom = 0.dp),
        ) {
            if (!isLgUp) {
                stickyHeader(key = "merchant") {
                    ProductDetailMerchantHeader(
                        merchant = product.merchant,
                        showVisitStore = true,
                        onVisitStoreClick = {
                            resolveStoreId(product.merchant)?.let(onStoreOpen)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(
                                horizontal = VitranSpacing.lg,
                                vertical = VitranSpacing.md,
                            ),
                    )
                }
            }

            if (isMdUp) {
                item(key = "media-info") {
                    // shop.app: `md:flex-row md:gap-space-40`
                    //   children: media `flex-1` | buy `md:w-[29em]` (464dp).
                    // VitranTheme forces RTL, so Start is on the right — same child
                    // order yields buy | gallery, matching an LTR mirror without
                    // swapping DOM or fighting layout with absolute offsets.
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = VitranSpacing.xxxl)
                            .padding(top = DesktopRowTopMargin),
                        horizontalArrangement = Arrangement.spacedBy(DesktopColumnGap),
                        verticalAlignment = Alignment.Top,
                    ) {
                        ProductDetailMediaSection(
                            media = product.media,
                            applyHorizontalInset = false,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .productDetailStickyGallery(
                                    listState = listState,
                                    itemKey = "media-info",
                                    stickyTop = VitranSpacing.xxxl,
                                    itemTopPad = DesktopRowTopMargin,
                                ),
                        )
                        ProductDetailInfoColumn(
                            product = product,
                            showMerchantHeader = isLgUp,
                            contentHorizontalPadding = false,
                            onVisitStoreClick = {
                                resolveStoreId(product.merchant)?.let(onStoreOpen)
                            },
                            modifier = Modifier
                                .widthIn(min = BuyColumnMinWidth, max = BuyColumnMaxWidth)
                                .width(BuyColumnWidth)
                                .padding(horizontal = InfoSideInset),
                        )
                    }
                }
            } else {
                item(key = "media") {
                    ProductDetailMediaSection(
                        media = product.media,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                item(key = "info") {
                    ProductDetailInfoColumn(
                        product = product,
                        showMerchantHeader = false,
                        contentHorizontalPadding = true,
                        onVisitStoreClick = {
                            resolveStoreId(product.merchant)?.let(onStoreOpen)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = VitranSpacing.lg),
                    )
                }
            }

            item(key = "recommendations") {
                ProductDetailRecommendationsSection(
                    product = product,
                    onProductClick = { clicked ->
                        onProductOpen(
                            clicked.id,
                            clicked.title,
                            clicked.imageUrl,
                            clicked.storeName,
                            clicked.priceLabel,
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = CategoriesSectionGap),
                )
            }

            item(key = "footer") {
                SiteFooter(
                    onLinkClick = { /* mock — footer destinations not wired yet */ },
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
    }
}

/** shop.app `md:w-[29em]` at 16px root. */
private val BuyColumnWidth = 464.dp

/** Floor so long titles do not crush into a stub column. */
private val BuyColumnMinWidth = 360.dp

/** Cap aligned with shop buy-column comfort width. */
private val BuyColumnMaxWidth = 520.dp

/** shop.app `md:gap-space-40` between gallery and buy column. */
private val DesktopColumnGap = 40.dp

/** Horizontal inset inside the buy column so info breathes from both sides. */
private val InfoSideInset = VitranSpacing.xxxl

/** shop.app `md:mt-space-24` on the media|info row. */
private val DesktopRowTopMargin = VitranSpacing.xxl

/** Prefer explicit [ProductDetailMerchant.shopId], else match Home shop by name. */
private fun resolveStoreId(merchant: ProductDetailMerchant): String? =
    merchant.shopId
        ?: allMockHomeShopCards()
            .firstOrNull { it.name.equals(merchant.name, ignoreCase = true) }
            ?.id
