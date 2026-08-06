package com.vitran.shop.ui.sections.product

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Mimics shop.app `md:sticky md:top-space-32 md:self-start` for the PDP gallery
 * inside a tall LazyColumn item (media|info row).
 *
 * While the item scrolls, the gallery translates so its top stays at [stickyTop]
 * in the LazyColumn viewport, then clamps so it leaves with the item bottom
 * once the buy/info column has scrolled past.
 */
fun Modifier.productDetailStickyGallery(
    listState: LazyListState,
    itemKey: Any,
    stickyTop: Dp = 32.dp,
    itemTopPad: Dp = 0.dp,
): Modifier = composed {
    val density = LocalDensity.current
    val stickyTopPx = with(density) { stickyTop.toPx() }
    val topPadPx = with(density) { itemTopPad.toPx() }
    var galleryHeightPx by remember { mutableFloatStateOf(0f) }

    val translationY by remember(listState, itemKey, stickyTopPx, topPadPx) {
        derivedStateOf {
            val item = listState.layoutInfo.visibleItemsInfo
                .firstOrNull { it.key == itemKey }
                ?: return@derivedStateOf 0f
            val galleryH = galleryHeightPx
            if (galleryH <= 0f) return@derivedStateOf 0f

            val naturalY = item.offset + topPadPx
            val raw = stickyTopPx - naturalY
            val maxT = (item.size - topPadPx - galleryH).coerceAtLeast(0f)
            raw.coerceIn(0f, maxT)
        }
    }

    this
        .onSizeChanged { galleryHeightPx = it.height.toFloat() }
        .graphicsLayer { this.translationY = translationY }
}
