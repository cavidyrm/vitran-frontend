package com.vitran.shop.ui.components.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing

@Composable
fun AdminFormCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    icon: Painter? = null,
    trailing: @Composable (() -> Unit)? = null,
    hasError: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(AdminTokens.CardRadius)
    val border = if (hasError) AdminTokens.ErrorBorder else AdminTokens.CardBorder
    Column(
        modifier = modifier
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
            .border(1.dp, border, shape)
            .padding(AdminTokens.CardPadding),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
    ) {
        if (title != null) {
            AdminCardHeader(
                title = title,
                subtitle = subtitle,
                icon = icon,
                trailing = trailing,
            )
        }
        content()
    }
}

@Composable
fun AdminCardHeader(
    title: String,
    subtitle: String? = null,
    icon: Painter? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
        verticalAlignment = Alignment.Top,
    ) {
        if (icon != null) {
            Box(
                modifier = Modifier
                    .size(32.dp)
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
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                ),
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = AdminTokens.Helper,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp, lineHeight = 18.sp),
                )
            }
        }
        if (trailing != null) {
            trailing()
        }
    }
}
