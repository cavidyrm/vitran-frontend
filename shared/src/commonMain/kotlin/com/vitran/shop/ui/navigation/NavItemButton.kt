package com.vitran.shop.ui.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.theme.VitranOpacity
import com.vitran.shop.ui.theme.VitranSpacing

@Composable
fun NavItemButton(
    painter: Painter,
    contentDescription: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showTooltip: Boolean = false,
    tooltipText: String = contentDescription,
    tint: Color? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val iconHovered by interactionSource.collectIsHoveredAsState()
    val pressed by interactionSource.collectIsPressedAsState()
    var overlayHovered by remember { mutableStateOf(false) }
    val hovered = iconHovered || overlayHovered
    val resolvedTint = tint ?: MaterialTheme.colorScheme.onSurface

    val targetOpacity = when {
        !enabled -> VitranOpacity.INACTIVE
        selected -> 1f
        hovered -> 1f
        else -> VitranOpacity.INACTIVE
    }
    val opacity by animateFloatAsState(
        targetValue = targetOpacity,
        animationSpec = tween(150),
        label = "navItemOpacity",
    )
    val targetScale = when {
        pressed -> 0.96f
        hovered && enabled -> 1.05f
        else -> 1f
    }
    val scale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(150),
        label = "navItemScale",
    )

    val iconContent: @Composable () -> Unit = {
        Box(
            modifier = Modifier
                .hoverable(interactionSource = interactionSource, enabled = true)
                .selectable(
                    selected = selected,
                    enabled = enabled,
                    role = Role.Tab,
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                )
                .padding(VitranSpacing.xs)
                .scale(scale),
            contentAlignment = Alignment.Center,
        ) {
            VitranIcon(
                painter = painter,
                contentDescription = contentDescription,
                // shop.app rail glyphs read ~20–24; keep slightly compact vs iconMedium.
                size = 20.dp,
                tint = resolvedTint.copy(alpha = opacity),
            )
        }
    }

    if (showTooltip) {
        NavTooltipOverlay(
            tooltipText = tooltipText,
            anchorHovered = iconHovered,
            enabled = enabled,
            modifier = modifier,
            onOverlayHoveredChange = { overlayHovered = it },
            content = iconContent,
        )
    } else {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            iconContent()
        }
    }
}
