package com.vitran.shop.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.theme.VitranAnimation
import com.vitran.shop.ui.theme.VitranElevation
import com.vitran.shop.ui.theme.VitranShapes
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing

private val TooltipShape = VitranShapes.pill

/**
 * Visual nav tooltip pill (desktop). Not an accessibility label — keep a11y on the icon.
 * Non-interactive: no click handlers; hover is owned by the parent overlay row when needed.
 */
@Composable
fun NavTooltip(
    text: String,
    visible: Boolean,
    modifier: Modifier = Modifier,
) {
    val durationMs = VitranAnimation.Tooltip.DURATION_MS
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(durationMs)) +
            slideInVertically(
                animationSpec = tween(durationMs),
                initialOffsetY = { -it / 4 },
            ),
        exit = fadeOut(animationSpec = tween(durationMs)) +
            slideOutVertically(
                animationSpec = tween(durationMs),
                targetOffsetY = { -it / 4 },
            ),
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .wrapContentWidth()
                .shadow(elevation = VitranElevation.small, shape = TooltipShape, clip = false)
                .background(MaterialTheme.colorScheme.surface, TooltipShape)
                .border(VitranSize.borderHairline, MaterialTheme.colorScheme.outline, TooltipShape)
                .padding(
                    horizontal = VitranSpacing.md,
                    vertical = VitranSpacing.xs,
                ),
            contentAlignment = Alignment.Center,
        ) {
            VitranText(
                text = text,
                style = VitranTextStyle.Label,
                maxLines = 1,
                softWrap = false,
            )
        }
    }
}
