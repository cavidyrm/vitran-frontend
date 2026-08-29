package com.vitran.shop.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitran.shop.di.vitranKoinViewModel
import com.vitran.shop.feature.taxonomy.presentation.CategoriesBrowseUiState
import com.vitran.shop.feature.taxonomy.presentation.CategoriesBrowseViewModel
import com.vitran.shop.ui.components.FloatingSearchFab
import com.vitran.shop.ui.components.FloatingSearchOmnibox
import com.vitran.shop.ui.components.OmniboxMobileSearchSheet
import com.vitran.shop.ui.components.OmniboxResult
import com.vitran.shop.ui.components.SiteFooter
import com.vitran.shop.ui.components.SiteFooterLinkId
import com.vitran.shop.ui.sections.categories.CategoriesBrowseCategoriesSection
import com.vitran.shop.ui.sections.categories.CategoriesExploreFeaturedSection
import com.vitran.shop.ui.sections.categories.CategoriesMerchantGridsFeed
import com.vitran.shop.ui.sections.categories.CategoriesProductRowsFeed
import com.vitran.shop.ui.sections.categories.CategoriesSectionGap
import com.vitran.shop.ui.sections.categories.rememberMockBrowseCategories
import com.vitran.shop.ui.sections.categories.rememberMockCategoriesMerchantGrids
import com.vitran.shop.ui.sections.categories.rememberMockCategoriesProductRows
import com.vitran.shop.ui.sections.categories.rememberMockExploreEdits
import com.vitran.shop.ui.sections.reference.ReferenceDataError
import com.vitran.shop.ui.sections.reference.ReferenceDataLoading
import com.vitran.shop.ui.sections.reference.toBrowseCategories
import com.vitran.shop.ui.shell.LocalDesktopLayout
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

/** shop.app mobile overlay `backdrop-blur-[10px]` — blur content under the sheet. */
private val MobileOmniboxBackdropBlur = 10.dp

/**
 * Categories (Explore) screen host. Sections are added one at a time.
 * Route: `/categories` ↔ shop.app Explore.
 *
 * Vertical rhythm matches shop.app `space-y-space-40` between major blocks.
 * [SiteFooter] owns its own top pad (48 compact / 136 desktop) — same as Home —
 * so it sits outside the spaced column to avoid doubling the gap.
 *
 * Floating search is always on (no hero omnibox to scroll past), matching shop.app
 * `/categories`. Compact FAB hides only while the mobile sheet is open.
 *
 * @param onProductOpen product id, title, image URL, store name, and price from a product-row click.
 * @param onStoreOpen store / merchant id from a merchant-grid shop click.
 */
@Composable
fun CategoriesScreen(
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
    browseViewModel: CategoriesBrowseViewModel = vitranKoinViewModel(),
) {
    val isDesktop = LocalDesktopLayout.current
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val edits = rememberMockExploreEdits()
    val mockBrowseCategories = rememberMockBrowseCategories()
    val browseState by browseViewModel.uiState.collectAsStateWithLifecycle()
    val browseCategories = when (val current = browseState) {
        is CategoriesBrowseUiState.Content -> current.rootCategories.toBrowseCategories()
        else -> mockBrowseCategories
    }
    val productRows = rememberMockCategoriesProductRows()
    val merchantGrids = rememberMockCategoriesMerchantGrids()

    var query by remember { mutableStateOf("") }
    var omniboxExpanded by remember { mutableStateOf(false) }
    var omniboxBoundsInRoot by remember { mutableStateOf(Rect.Zero) }
    var screenOriginInRoot by remember { mutableStateOf(Offset.Zero) }

    // Always visible on Categories; hide compact FAB only while the sheet is open.
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
                    if (!isDesktop && omniboxExpanded) {
                        Modifier.blur(MobileOmniboxBackdropBlur)
                    } else {
                        Modifier
                    },
                )
                .verticalScroll(
                    state = scrollState,
                    enabled = isDesktop || !omniboxExpanded,
                ),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(CategoriesSectionGap),
            ) {
                CategoriesExploreFeaturedSection(
                    edits = edits,
                    modifier = Modifier.fillMaxWidth(),
                    onEditClick = { /* mock — collection landing not wired yet */ },
                )
                when (val current = browseState) {
                    CategoriesBrowseUiState.Loading -> {
                        ReferenceDataLoading(
                            message = "در حال بارگذاری دسته‌بندی‌ها…",
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    is CategoriesBrowseUiState.Error -> {
                        ReferenceDataError(
                            message = current.message,
                            onRetry = browseViewModel::retry,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    else -> {
                        CategoriesBrowseCategoriesSection(
                            categories = browseCategories,
                            modifier = Modifier.fillMaxWidth(),
                            onCategoryClick = { /* mock — category landing not wired yet */ },
                        )
                    }
                }
                CategoriesProductRowsFeed(
                    sections = productRows,
                    modifier = Modifier.fillMaxWidth(),
                    onSectionClick = { /* mock — category / collection landing not wired yet */ },
                    onProductClick = { _, product ->
                        onProductOpen(
                            product.id,
                            product.title,
                            product.imageUrl,
                            product.storeName,
                            product.priceLabel,
                        )
                    },
                    onSaveClick = { _, _ -> /* mock — saved items not wired yet */ },
                )
                CategoriesMerchantGridsFeed(
                    sections = merchantGrids,
                    modifier = Modifier.fillMaxWidth(),
                    onSectionClick = { /* mock — category landing not wired yet */ },
                    onShopClick = { _, shop -> onStoreOpen(shop.id) },
                )
            }
            SiteFooter(
                onLinkClick = onFooterLinkClick,
                onLanguageClick = { /* mock — language settings not wired yet */ },
                onDownloadClick = { /* mock — store deep link not wired yet */ },
                modifier = Modifier.fillMaxWidth(),
            )
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
