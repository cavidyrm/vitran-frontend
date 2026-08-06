package com.vitran.shop.ui.sections.product

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.vitran.shop.ui.theme.VitranSize
import kotlinx.coroutines.launch

/**
 * shop.app PDP side sheet host (Description / Reviews / policies).
 *
 * Measured on live shop.app (LTR docks to physical **right** with
 * `translate-x-full` ↔ `none`, `transition-transform 300ms ease-out`).
 * VitranShop is RTL, so this mirrors to the physical **left**:
 * - closed: `translationX = -width`
 * - open: `translationX = 0`
 *
 * Only **translateX** moves the panel (no scale-from-center). Scrim fades alone.
 */
@Composable
fun ProductDetailSideSheet(
    onDismiss: () -> Unit,
    content: @Composable (onClose: () -> Unit) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val scrimAlpha = remember { Animatable(0f) }
    val slidePx = remember { Animatable(0f) }
    var panelWidthPx by remember { mutableIntStateOf(0) }
    var panelReady by remember { mutableStateOf(false) }
    var closing by remember { mutableStateOf(false) }

    fun offscreenX(): Float = -panelWidthPx.toFloat().coerceAtLeast(1f)

    fun requestClose() {
        if (closing) return
        closing = true
        scope.launch {
            launch {
                scrimAlpha.animateTo(
                    0f,
                    tween(SideSheetAnimMs, easing = SideSheetEasing),
                )
            }
            slidePx.animateTo(
                offscreenX(),
                tween(SideSheetAnimMs, easing = SideSheetEasing),
            )
            onDismiss()
        }
    }

    LaunchedEffect(panelWidthPx) {
        if (panelWidthPx == 0 || closing) return@LaunchedEffect
        slidePx.snapTo(offscreenX())
        panelReady = true
        launch {
            scrimAlpha.animateTo(
                1f,
                tween(SideSheetAnimMs, easing = SideSheetEasing),
            )
        }
        slidePx.animateTo(
            0f,
            tween(SideSheetAnimMs, easing = SideSheetEasing),
        )
    }

    Dialog(
        onDismissRequest = { requestClose() },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false,
        ),
    ) {
        SuppressPlatformDialogEnterExit()

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isDesktop = maxWidth >= VitranSize.desktopBreakpoint

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = scrimAlpha.value }
                    .background(SideSheetScrim)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        role = Role.Button,
                        onClick = { requestClose() },
                    ),
            )

            Box(
                modifier = Modifier
                    .align(AbsoluteAlignment.CenterLeft)
                    .then(
                        if (isDesktop) {
                            Modifier
                                .absolutePadding(
                                    left = SideSheetEdgeInset,
                                    top = SideSheetEdgeInset,
                                    bottom = SideSheetEdgeInset,
                                    right = SideSheetEdgeInset,
                                )
                                .width(SideSheetDesktopWidth)
                                .fillMaxHeight()
                        } else {
                            Modifier.fillMaxSize()
                        },
                    )
                    .onSizeChanged { size ->
                        if (size.width > 0 && panelWidthPx == 0) {
                            panelWidthPx = size.width
                        }
                    }
                    // Hide until measured+snapped off-screen so the first frame is not centered.
                    .graphicsLayer {
                        alpha = if (panelReady) 1f else 0f
                        translationX = slidePx.value
                    }
                    .shadow(
                        elevation = if (isDesktop) SideSheetShadowElevation else 0.dp,
                        shape = if (isDesktop) {
                            RoundedCornerShape(SideSheetDesktopRadius)
                        } else {
                            RoundedCornerShape(0.dp)
                        },
                        spotColor = SideSheetShadowColor,
                        ambientColor = SideSheetShadowColor,
                    )
                    .clip(
                        if (isDesktop) {
                            RoundedCornerShape(SideSheetDesktopRadius)
                        } else {
                            RoundedCornerShape(0.dp)
                        },
                    )
                    .background(SideSheetPanelFill),
            ) {
                content(::requestClose)
            }
        }
    }
}

private const val SideSheetAnimMs = 300
/** shop.app `cubic-bezier(0, 0, 0.2, 1)` / `ease-out`. */
private val SideSheetEasing = CubicBezierEasing(0f, 0f, 0.2f, 1f)
private val SideSheetScrim = Color.Black.copy(alpha = 0.5f)
/** shop.app inner panel ~500dp, outer shell ~508 with 8dp inset. */
private val SideSheetDesktopWidth = 500.dp
private val SideSheetDesktopRadius = 24.dp
private val SideSheetEdgeInset = 8.dp
private val SideSheetShadowElevation = 24.dp
private val SideSheetShadowColor = Color.Black.copy(alpha = 0.12f)
/** shop.app `rgba(255,255,255,0.9)` + blur ≈ opaque frost. */
private val SideSheetPanelFill = Color.White.copy(alpha = 0.96f)
