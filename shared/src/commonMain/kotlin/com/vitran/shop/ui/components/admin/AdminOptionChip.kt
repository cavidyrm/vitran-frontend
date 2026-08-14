package com.vitran.shop.ui.components.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun AdminTextButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: Painter? = null,
    destructive: Boolean = false,
) {
    val color = if (destructive) AdminTokens.Destructive else AdminTokens.Brand
    Row(
        modifier = modifier
            .heightIn(min = VitranSize.touchTarget)
            .clip(RoundedCornerShape(VitranRadius.small))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = VitranSpacing.sm, vertical = VitranSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        if (leadingIcon != null) {
            VitranIcon(
                painter = leadingIcon,
                contentDescription = null,
                size = VitranSize.iconSmall,
                tint = color,
            )
        }
        Text(
            text = label,
            color = color,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
            ),
        )
    }
}

@Composable
fun AdminSecondaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val shape = RoundedCornerShape(VitranRadius.small)
    Box(
        modifier = modifier
            .heightIn(min = AdminTokens.SaveHeight)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, AdminTokens.FieldBorder, shape)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = VitranSpacing.lg, vertical = VitranSpacing.sm),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
        )
    }
}

@Composable
fun AdminPrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    fillMaxWidth: Boolean = false,
) {
    val shape = RoundedCornerShape(VitranRadius.small)
    val background = if (enabled) AdminTokens.Brand else AdminTokens.FieldBorder
    Box(
        modifier = modifier
            .then(if (fillMaxWidth) Modifier.fillMaxWidth() else Modifier)
            .heightIn(min = AdminTokens.SaveHeight)
            .clip(shape)
            .background(background)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = VitranSpacing.xl, vertical = VitranSpacing.sm),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = if (enabled) AdminTokens.OnBrand else MaterialTheme.colorScheme.surface,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
        )
    }
}

@Composable
fun AdminOptionChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    value: String? = null,
) {
    val shape = RoundedCornerShape(percent = 50)
    Row(
        modifier = modifier
            .heightIn(min = 32.dp)
            .clip(shape)
            .background(if (selected) AdminTokens.DropdownHover else MaterialTheme.colorScheme.surface)
            .border(1.dp, AdminTokens.FieldBorder, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = VitranSpacing.md, vertical = VitranSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
            ),
        )
        if (value != null) {
            Text(
                text = value,
                color = AdminTokens.Helper,
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
            )
        }
    }
}

@Composable
fun AdminTextLink(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AdminTextButton(label = label, onClick = onClick, modifier = modifier)
}
