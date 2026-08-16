package com.vitran.shop.ui.sections.account

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.theme.VitranElevation
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.ic_check

/**
 * Anchored select menu — width matches the field, opens just below it
 * (same Popup pattern as [com.vitran.shop.ui.components.admin.AdminSelect]).
 */
@Composable
internal fun AccountSelectMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    menuWidth: Dp,
    anchorHeightPx: Int,
) {
    if (!expanded || menuWidth <= 0.dp || anchorHeightPx <= 0) return

    val density = LocalDensity.current
    val gapPx = with(density) { 4.dp.roundToPx() }

    Popup(
        alignment = Alignment.TopStart,
        offset = IntOffset(0, anchorHeightPx + gapPx),
        onDismissRequest = onDismiss,
        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        ),
    ) {
        val shape = RoundedCornerShape(VitranRadius.medium)
        Column(
            modifier = Modifier
                .width(menuWidth)
                .shadow(
                    elevation = VitranElevation.medium,
                    shape = shape,
                    clip = false,
                    ambientColor = Color.Black.copy(alpha = 0.12f),
                    spotColor = Color.Black.copy(alpha = 0.12f),
                )
                .clip(shape)
                .background(MaterialTheme.colorScheme.surface, shape)
                .border(1.dp, AccountTokens.CardBorder, shape),
        ) {
            options.forEach { option ->
                val isSelected = option == selected
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isSelected) AccountTokens.MenuHover else Color.Transparent)
                        .clickable(role = Role.Button) {
                            onSelect(option)
                            onDismiss()
                        }
                        .padding(
                            horizontal = VitranSpacing.lg,
                            vertical = VitranSpacing.md,
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    VitranText(
                        text = option,
                        style = VitranTextStyle.Body,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                    )
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onSurface),
                            contentAlignment = Alignment.Center,
                        ) {
                            VitranIcon(
                                painter = painterResource(Res.drawable.ic_check),
                                contentDescription = null,
                                size = VitranSize.iconSmall,
                                tint = MaterialTheme.colorScheme.surface,
                            )
                        }
                    }
                }
            }
        }
    }
}
