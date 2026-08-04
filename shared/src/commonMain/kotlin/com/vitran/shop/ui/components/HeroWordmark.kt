package com.vitran.shop.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vitran.shop.ui.shell.LocalDesktopLayout
import com.vitran.shop.ui.theme.ShopPurple
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.hero_wordmark_a11y
import vitranshop.shared.generated.resources.ic_vitran_wordmark

/** shop.app wordmark: `h-space-32` compact, `md:h-[72px]` desktop. */
private val WordmarkHeightCompact = 32.dp
private val WordmarkHeightDesktop = 72.dp

/** Intrinsic aspect of [ic_vitran_wordmark] (viewport 114 × 42). */
private const val WordmarkAspect = 114f / 42f

/**
 * Large brand wordmark for the Home hero (shop.app `heroContainer` logo).
 * Sized like shop.app's Shop SVG: fixed height, width from aspect ratio.
 */
@Composable
fun HeroWordmark(
    modifier: Modifier = Modifier,
) {
    val a11y = stringResource(Res.string.hero_wordmark_a11y)
    val height = if (LocalDesktopLayout.current) {
        WordmarkHeightDesktop
    } else {
        WordmarkHeightCompact
    }

    Image(
        painter = painterResource(Res.drawable.ic_vitran_wordmark),
        contentDescription = a11y,
        modifier = modifier.size(width = height * WordmarkAspect, height = height),
        contentScale = ContentScale.Fit,
        // Vector fill is black for reliable tinting across targets.
        colorFilter = ColorFilter.tint(ShopPurple),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFBFBFB)
@Composable
private fun HeroWordmarkCompactPreview() {
    VitranTheme {
        Box(modifier = Modifier.padding(VitranSpacing.xl)) {
            HeroWordmark()
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFBFBFB, widthDp = 400)
@Composable
private fun HeroWordmarkDesktopPreview() {
    VitranTheme {
        CompositionLocalProvider(LocalDesktopLayout provides true) {
            Box(modifier = Modifier.padding(VitranSpacing.xl)) {
                HeroWordmark()
            }
        }
    }
}
