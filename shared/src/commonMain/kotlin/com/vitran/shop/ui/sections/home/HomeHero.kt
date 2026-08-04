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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.vitran.shop.ui.components.HeroOmnibox
import com.vitran.shop.ui.components.HeroWordmark
import com.vitran.shop.ui.shell.LocalDesktopLayout
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme

/** Soft pull of wordmark/omnibox into collage (shop.app `lg:-mb-space-64`). */
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
 */
@Composable
fun HomeHero(
    modifier: Modifier = Modifier,
    scenes: List<HeroCollageScene> = MockHeroCollageScenes,
    onOmniboxExpandedChange: (Boolean) -> Unit = {},
    onOmniboxBoundsInRoot: (Rect) -> Unit = {},
    onOmniboxDismissHandlerReady: ((() -> Unit) -> Unit) = {},
) {
    val isDesktop = LocalDesktopLayout.current
    val focusManager = LocalFocusManager.current
    var query by remember { mutableStateOf("") }
    var omniboxExpanded by remember { mutableStateOf(false) }

    fun dismissOmnibox() {
        query = ""
        omniboxExpanded = false
        onOmniboxExpandedChange(false)
        focusManager.clearFocus()
    }

    SideEffect {
        onOmniboxDismissHandlerReady { dismissOmnibox() }
    }

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
                                onClick = { dismissOmnibox() },
                            ),
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(3f)
                .offset(y = if (isDesktop) DesktopHeroOverlap else 0.dp)
                .padding(
                    start = VitranSpacing.lg,
                    end = VitranSpacing.lg,
                    top = if (isDesktop) VitranSpacing.md else CompactTopBelowBanner,
                    bottom = VitranSpacing.xl,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box {
                HeroWordmark()
                if (omniboxExpanded) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { dismissOmnibox() },
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
                onQueryChange = { query = it },
                onSubmit = { /* mock — search screen not wired yet */ },
                expanded = omniboxExpanded,
                onExpandedChange = {
                    omniboxExpanded = it
                    onOmniboxExpandedChange(it)
                },
                onBoundsInRoot = onOmniboxBoundsInRoot,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 280)
@Composable
private fun HomeHeroCompactPreview() {
    VitranTheme {
        HomeHero()
    }
}

@Preview(showBackground = true, widthDp = 1200, heightDp = 520)
@Composable
private fun HomeHeroDesktopPreview() {
    VitranTheme {
        CompositionLocalProvider(LocalDesktopLayout provides true) {
            HomeHero()
        }
    }
}
