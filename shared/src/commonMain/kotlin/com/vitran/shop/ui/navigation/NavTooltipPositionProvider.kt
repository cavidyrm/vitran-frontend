package com.vitran.shop.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupPositionProvider

/**
 * Pure geometry helper for nav tooltips — no hover/overlay state.
 *
 * Positions the tooltip toward the content side of the rail:
 * - LTR (rail left): flush to the right of the anchor.
 * - RTL (rail right): flush to the left of the anchor.
 *
 * Callers that include a hover bridge should size it with [NavTooltipBridgeWidth];
 * that width is part of [popupContentSize], so this provider stays layout-agnostic.
 */
@Composable
fun rememberNavTooltipPositionProvider(): PopupPositionProvider {
    return remember {
        object : PopupPositionProvider {
            override fun calculatePosition(
                anchorBounds: IntRect,
                windowSize: IntSize,
                layoutDirection: LayoutDirection,
                popupContentSize: IntSize,
            ): IntOffset {
                val y = anchorBounds.top + (anchorBounds.height - popupContentSize.height) / 2
                val x = when (layoutDirection) {
                    LayoutDirection.Ltr -> anchorBounds.right
                    LayoutDirection.Rtl -> anchorBounds.left - popupContentSize.width
                }
                return IntOffset(x, y)
            }
        }
    }
}

/** Hover bridge between rail icon and tooltip pill; measured inside the Popup, not the rail. */
internal val NavTooltipBridgeWidth = 6.dp
