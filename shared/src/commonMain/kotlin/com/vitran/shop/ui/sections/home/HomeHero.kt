package com.vitran.shop.ui.sections.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.vitran.shop.ui.components.HeroOmnibox
import com.vitran.shop.ui.components.HeroWordmark
import com.vitran.shop.ui.shell.LocalDesktopLayout
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme

/**
 * Soft pull of wordmark/omnibox into collage (shop.app `lg:-mb-space-64`).
 * Negative value: visual offset up. Magnitude is also subtracted from layout
 * height so following content (categories) is not left with a 64dp empty gap.
 */
private val DesktopHeroOverlap = (-64).dp

/** shop.app desktop logo→omnibox gap (comfortable space under wordmark). */
private val LogoToOmniboxDesktop = 48.dp

/** shop.app compact logo→omnibox gap (`mt-space-12` / ~24 after wordmark). */
private val LogoToOmniboxCompact = VitranSpacing.xxl

/**
 * Compact: download banner overlays hero; keep wordmark below it with shop.app
 * gap (~20px under the 40px strip) → top inset 60dp.
 */
private val CompactTopBelowBanner = VitranSize.downloadBannerHeight + 20.dp

/**
 * Home hero block: desktop collage + brand wordmark + search omnibox.
 * Collage is gated by [LocalDesktopLayout] (shell viewport breakpoint).
 *
 * Omnibox query/expanded state is owned by [HomeScreen] so the compact mobile
 * sheet can render above the scroll container.
 */
@Composable
fun HomeHero(
    query: String,
    onQueryChange: (String) -> Unit,
    omniboxExpanded: Boolean,
    onOmniboxExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    scenes: List<HeroCollageScene> = MockHeroCollageScenes,
    onOmniboxBoundsInRoot: (Rect) -> Unit = {},
    onOmniboxCollapsedLayoutCoordinates: (LayoutCoordinates) -> Unit = {},
    onOmniboxDismiss: () -> Unit = {},
) {
    val isDesktop = LocalDesktopLayout.current

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (isDesktop) {
            Box(modifier = Modifier.fillMaxWidth()) {
                HeroCollage(
                    scenes = scenes,
                    modifier = Modifier.fillMaxWidth(),
                    onBrandClick = { /* mock */ },
                    onProductClick = { /* mock */ },
                )
                if (omniboxExpanded) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .zIndex(4f)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onOmniboxDismiss,
                            ),
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                // Absolute typeahead must paint above collage dismiss scrim (zIndex 4).
                .zIndex(if (omniboxExpanded && isDesktop) 20f else 3f)
                .then(
                    if (isDesktop) {
                        // Visual pull into collage + shrink layout height (CSS -mb-space-64).
                        Modifier
                            .offset(y = DesktopHeroOverlap)
                            .layout { measurable, constraints ->
                                val placeable = measurable.measure(constraints)
                                val overlapPx = (-DesktopHeroOverlap).roundToPx()
                                layout(
                                    width = placeable.width,
                                    height = (placeable.height - overlapPx).coerceAtLeast(0),
                                ) {
                                    placeable.placeRelative(0, 0)
                                }
                            }
                    } else {
                        Modifier
                    },
                )
                .padding(
                    start = VitranSpacing.lg,
                    end = VitranSpacing.lg,
                    top = if (isDesktop) VitranSpacing.md else CompactTopBelowBanner,
                    bottom = 0.dp,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box {
                HeroWordmark()
                if (omniboxExpanded && isDesktop) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onOmniboxDismiss,
                            ),
                    )
                }
            }
            Spacer(
                modifier = Modifier.height(
                    if (isDesktop) LogoToOmniboxDesktop else LogoToOmniboxCompact,
                ),
            )
            HeroOmnibox(
                query = query,
                onQueryChange = onQueryChange,
                onSubmit = { /* mock — search screen not wired yet */ },
                expanded = omniboxExpanded,
                onExpandedChange = onOmniboxExpandedChange,
                onBoundsInRoot = onOmniboxBoundsInRoot,
                onCollapsedLayoutCoordinates = onOmniboxCollapsedLayoutCoordinates,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 280)
@Composable
private fun HomeHeroCompactPreview() {
    VitranTheme {
        HomeHero(
            query = "",
            onQueryChange = {},
            omniboxExpanded = false,
            onOmniboxExpandedChange = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 1200, heightDp = 520)
@Composable
private fun HomeHeroDesktopPreview() {
    VitranTheme {
        CompositionLocalProvider(LocalDesktopLayout provides true) {
            HomeHero(
                query = "",
                onQueryChange = {},
                omniboxExpanded = false,
                onOmniboxExpandedChange = {},
            )
        }
    }
}
