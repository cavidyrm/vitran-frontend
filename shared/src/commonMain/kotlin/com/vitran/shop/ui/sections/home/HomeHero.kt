package com.vitran.shop.ui.sections.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.vitran.shop.ui.components.HeroOmnibox
import com.vitran.shop.ui.components.HeroWordmark
import com.vitran.shop.ui.shell.LocalDesktopLayout
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme

/** Soft pull of wordmark/omnibox into collage (shop.app `lg:-mb-space-64`). */
private val DesktopHeroOverlap = (-64).dp

/**
 * Home hero block: desktop collage + brand wordmark + search omnibox.
 * Collage is gated by [LocalDesktopLayout] (shell viewport breakpoint).
 */
@Composable
fun HomeHero(
    modifier: Modifier = Modifier,
    scenes: List<HeroCollageScene> = MockHeroCollageScenes,
) {
    val isDesktop = LocalDesktopLayout.current
    var query by remember { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (isDesktop) {
            HeroCollage(
                scenes = scenes,
                modifier = Modifier.fillMaxWidth(),
                onBrandClick = { /* mock */ },
                onProductClick = { /* mock */ },
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(3f)
                .offset(y = if (isDesktop) DesktopHeroOverlap else 0.dp)
                .padding(
                    start = VitranSpacing.lg,
                    end = VitranSpacing.lg,
                    top = if (isDesktop) VitranSpacing.md else VitranSpacing.xl,
                    bottom = VitranSpacing.xl,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            HeroWordmark()
            Spacer(modifier = Modifier.height(VitranSpacing.md))
            HeroOmnibox(
                query = query,
                onQueryChange = { query = it },
                onSubmit = { /* mock — search screen not wired yet */ },
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
        // Preview has no AppShell — force desktop collage path.
        CompositionLocalProvider(LocalDesktopLayout provides true) {
            HomeHero()
        }
    }
}
