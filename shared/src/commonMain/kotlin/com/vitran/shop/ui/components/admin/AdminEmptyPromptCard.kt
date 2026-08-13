package com.vitran.shop.ui.components.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing

@Composable
fun AdminEmptyPromptCard(
    title: String,
    body: String,
    actionLabel: String,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    emoji: String? = null,
    emphasized: Boolean = true,
    onAction: () -> Unit = {},
) {
    val shape = RoundedCornerShape(AdminTokens.CardRadius)
    val cardModifier = modifier
        .fillMaxWidth()
        .shadow(
            elevation = AdminTokens.CardElevation,
            shape = shape,
            clip = false,
            ambientColor = AdminTokens.CardShadow,
            spotColor = AdminTokens.CardShadow,
        )
        .clip(shape)
        .background(MaterialTheme.colorScheme.surface, shape)
        .border(1.dp, AdminTokens.CardBorder, shape)
        .padding(AdminTokens.CardPadding)
    if (emoji != null) {
        Column(
            modifier = cardModifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        ) {
            Text(text = emoji, fontSize = 36.sp)
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                ),
            )
            Text(
                text = body,
                color = AdminTokens.Helper,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp, lineHeight = 18.sp),
            )
            EmptyAction(
                label = actionLabel,
                emphasized = emphasized,
                onAction = onAction,
            )
        }
        return
    }
    Row(
        modifier = cardModifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
    ) {
        if (icon != null) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(AdminTokens.DropdownHover),
                contentAlignment = Alignment.Center,
            ) {
                VitranIcon(
                    painter = icon,
                    contentDescription = null,
                    size = VitranSize.iconMedium,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                ),
            )
            Text(
                text = body,
                color = AdminTokens.Helper,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp, lineHeight = 18.sp),
            )
            EmptyAction(
                label = actionLabel,
                emphasized = emphasized,
                onAction = onAction,
            )
        }
    }
}

@Composable
private fun EmptyAction(
    label: String,
    emphasized: Boolean,
    onAction: () -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(top = VitranSpacing.sm)
            .height(36.dp)
            .clip(RoundedCornerShape(VitranRadius.small))
            .then(
                if (emphasized) {
                    Modifier.background(AdminTokens.SaveFill)
                } else {
                    Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, AdminTokens.FieldBorder, RoundedCornerShape(VitranRadius.small))
                },
            )
            .clickable(role = Role.Button, onClick = onAction)
            .padding(horizontal = VitranSpacing.lg),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = if (emphasized) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
            ),
        )
    }
}
