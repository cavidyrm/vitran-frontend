package com.vitran.shop.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.vitran.shop.ui.theme.VitranAnimation
import com.vitran.shop.ui.theme.VitranElevation
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.floating_search_fab_a11y
import vitranshop.shared.generated.resources.ic_search

/** shop.app compact FAB `size-space-48`. */
private val FabSize = 48.dp

/** shop.app `border 2px solid rgba(0,0,0,0.04)`. */
private val FabBorder = Color.Black.copy(alpha = 0.04f)

/** shop.app fill `rgba(255,255,255,0.9)`. */
private val FabFill = Color.White.copy(alpha = 0.9f)

/** shop.app inset white ring `1.5px`. */
private val FabInsetRingWidth = 1.5.dp

/**
 * Compact floating search button — shop.app
 * `fixed bottom-[86px] right-space-16 size-space-48`.
 *
 * Placed inside [com.vitran.shop.ui.shell.OmniboxOverlayHost] so bottom inset
 * clears [com.vitran.shop.ui.navigation.AppBottomNav] (≈ nav + 16dp gap).
 *
 * Show/hide via `translateY` only (always composed).
 */
@Composable
fun FloatingSearchFab(
    visible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val a11y = stringResource(Res.string.floating_search_fab_a11y)
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val hideOffsetPx = with(density) {
        (FabSize + VitranAnimation.Omnibox.FLOAT_FAB_HIDE_EXTRA_DP.dp).toPx()
    }
    val translationY by animateFloatAsState(
        targetValue = if (visible) 0f else hideOffsetPx,
        animationSpec = tween(
            durationMillis = VitranAnimation.Omnibox.FLOAT_ENTER_MS,
            easing = VitranAnimation.Omnibox.FloatEasing,
        ),
        label = "floatingSearchFabY",
    )
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "floatingSearchFabScale",
    )

    Box(
        modifier = modifier
            .padding(end = VitranSpacing.lg, bottom = VitranSpacing.lg)
            .size(FabSize)
            .graphicsLayer {
                this.translationY = translationY
                scaleX = pressScale
                scaleY = pressScale
            }
            .shadow(
                elevation = VitranElevation.medium,
                shape = CircleShape,
                clip = false,
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f),
            )
            .border(width = 2.dp, color = FabBorder, shape = CircleShape)
            .clip(CircleShape)
            .background(FabFill)
            .drawBehind {
                // Inset white ring (shop.app `box-shadow: inset 0 0 0 1.5px #fff`).
                val ringPx = FabInsetRingWidth.toPx()
                val inset = ringPx / 2f
                drawCircle(
                    color = Color.White,
                    radius = size.minDimension / 2f - inset,
                    style = Stroke(width = ringPx),
                )
            }
            .clickable(
                enabled = visible,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .semantics { contentDescription = a11y },
        contentAlignment = Alignment.Center,
    ) {
        VitranIcon(
            painter = painterResource(Res.drawable.ic_search),
            contentDescription = null,
            size = VitranSize.iconMedium,
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
}
