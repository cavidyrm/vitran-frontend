package com.vitran.shop.ui.navigation

import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.vitran.shop.ui.theme.VitranAnimation
import com.vitran.shop.ui.theme.VitranSize
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

/**
 * Hosts [content] for rail measurement and shows [tooltipText] in a floating Popup
 * that does not participate in the [VitranSize.sideRailWidth] rail width.
 *
 * Positioning is owned solely by [rememberNavTooltipPositionProvider] (no hover state).
 */
@Composable
fun NavTooltipOverlay(
    tooltipText: String,
    anchorHovered: Boolean,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    onOverlayHoveredChange: (Boolean) -> Unit = {},
    content: @Composable () -> Unit,
) {
    val overlayInteraction = remember { MutableInteractionSource() }
    val overlayHovered by overlayInteraction.collectIsHoveredAsState()
    val layoutDirection = LocalLayoutDirection.current
    val positionProvider = rememberNavTooltipPositionProvider()

    LaunchedEffect(overlayHovered) {
        onOverlayHoveredChange(overlayHovered)
    }

    // Keep the Popup (and hover bridge) mounted briefly after leave so the cursor can reach the tooltip.
    var isPopupVisible by remember { mutableStateOf(false) }
    LaunchedEffect(anchorHovered, overlayHovered, enabled) {
        val active = enabled && (anchorHovered || overlayHovered)
        if (active) {
            isPopupVisible = true
        } else {
            delay(VitranAnimation.Tooltip.DISMISS_DELAY_MS.milliseconds)
            if (!anchorHovered && !overlayHovered) {
                isPopupVisible = false
            }
        }
    }

    val isTooltipContentVisible = enabled && (anchorHovered || overlayHovered)

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        content()

        if (isPopupVisible) {
            Popup(
                popupPositionProvider = positionProvider,
                properties = PopupProperties(
                    focusable = false,
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false,
                ),
            ) {
                Row(
                    modifier = Modifier.hoverable(overlayInteraction),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (layoutDirection == LayoutDirection.Ltr) {
                        Box(Modifier.width(NavTooltipBridgeWidth).height(VitranSize.touchTarget))
                        NavTooltip(text = tooltipText, visible = isTooltipContentVisible)
                    } else {
                        NavTooltip(text = tooltipText, visible = isTooltipContentVisible)
                        Box(Modifier.width(NavTooltipBridgeWidth).height(VitranSize.touchTarget))
                    }
                }
            }
        }
    }
}
