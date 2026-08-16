package com.vitran.shop.ui.sections.account

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.theme.ErrorRed
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing

@Composable
internal fun AccountCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(AccountTokens.CardRadius)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = AccountTokens.CardElevation,
                shape = shape,
                clip = false,
                ambientColor = AccountTokens.CardShadow,
                spotColor = AccountTokens.CardShadow,
            )
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(width = 1.dp, color = AccountTokens.CardBorder, shape = shape),
        content = content,
    )
}

@Composable
internal fun AccountFieldRow(
    label: String,
    modifier: Modifier = Modifier,
    value: String = "",
    placeholder: String? = null,
    showDivider: Boolean = true,
    editable: Boolean = false,
    onValueChange: (String) -> Unit = {},
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    val valueColor = if (value.isBlank()) {
        AccountTokens.Placeholder
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    val display = value.ifBlank { placeholder.orEmpty() }
    val textStyle = MaterialTheme.typography.bodyMedium.copy(
        fontSize = 15.sp,
        fontWeight = FontWeight.Normal,
        color = valueColor,
    )
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = AccountTokens.FieldMinHeight)
                .then(
                    if (onClick != null) {
                        Modifier.clickable(role = Role.Button, onClick = onClick)
                    } else {
                        Modifier
                    },
                )
                .padding(horizontal = VitranSpacing.lg),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            VitranText(
                text = label,
                style = VitranTextStyle.Body,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            if (editable) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = textStyle.copy(
                        color = if (value.isBlank()) {
                            AccountTokens.Placeholder
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        textAlign = TextAlign.End,
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    decorationBox = { inner ->
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterEnd,
                        ) {
                            if (value.isBlank() && placeholder != null) {
                                VitranText(
                                    text = placeholder,
                                    style = VitranTextStyle.Body,
                                    color = AccountTokens.Placeholder,
                                )
                            }
                            inner()
                        }
                    },
                    modifier = Modifier.weight(1.2f),
                )
            } else {
                Box(
                    modifier = Modifier.weight(1.2f),
                    contentAlignment = Alignment.CenterEnd,
                ) {
                    VitranText(
                        text = display,
                        style = VitranTextStyle.Body,
                        color = valueColor,
                        maxLines = 1,
                    )
                }
            }
            if (trailing != null) {
                Spacer(Modifier.width(VitranSpacing.sm))
                trailing()
            }
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = VitranSpacing.lg),
                thickness = VitranSize.borderHairline,
                color = AccountTokens.FieldDivider,
            )
        }
    }
}

@Composable
internal fun AccountTrailingIcon(
    painter: Painter,
    contentDescription: String?,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    VitranIcon(
        painter = painter,
        contentDescription = contentDescription,
        size = VitranSize.iconSmall,
        tint = tint,
    )
}

@Composable
internal fun AccountSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    hint: String? = null,
    icon: Painter? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
    ) {
        if (icon != null) {
            Box(
                modifier = Modifier
                    .size(AccountTokens.SectionIconTile)
                    .clip(RoundedCornerShape(VitranRadius.medium))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center,
            ) {
                VitranIcon(
                    painter = icon,
                    contentDescription = null,
                    size = VitranSize.iconMedium,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
        ) {
            VitranText(
                text = title,
                style = VitranTextStyle.Title,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (!hint.isNullOrBlank()) {
                VitranText(
                    text = hint,
                    style = VitranTextStyle.Body,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
internal fun AccountPrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
) {
    val shape = RoundedCornerShape(VitranRadius.medium)
    Row(
        modifier = modifier
            .height(VitranSize.buttonHeight)
            .clip(shape)
            .background(MaterialTheme.colorScheme.primary, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = VitranSpacing.xl),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        if (icon != null) {
            VitranIcon(
                painter = icon,
                contentDescription = null,
                size = VitranSize.iconSmall,
                tint = MaterialTheme.colorScheme.onPrimary,
            )
        }
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
        )
    }
}

@Composable
internal fun AccountOutlinedButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    borderColor: Color = AccountTokens.CardBorder,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    val shape = RoundedCornerShape(VitranRadius.medium)
    Row(
        modifier = modifier
            .height(VitranSize.buttonHeight)
            .clip(shape)
            .border(1.dp, borderColor, shape)
            .background(MaterialTheme.colorScheme.surface, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = VitranSpacing.lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (icon != null) {
            VitranIcon(
                painter = icon,
                contentDescription = null,
                size = VitranSize.iconSmall,
                tint = contentColor,
            )
            Spacer(modifier = Modifier.width(VitranSpacing.sm))
        }
        Text(
            text = label,
            color = contentColor,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
        )
    }
}

@Composable
internal fun AccountSoftIcon(
    painter: Painter,
    modifier: Modifier = Modifier,
    destructive: Boolean = false,
) {
    val background = if (destructive) AccountTokens.DangerSoft else AccountTokens.SoftIconBg
    val tint = if (destructive) ErrorRed else MaterialTheme.colorScheme.primary
    Box(
        modifier = modifier
            .size(AccountTokens.SectionIconTile)
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center,
    ) {
        VitranIcon(
            painter = painter,
            contentDescription = null,
            size = 20.dp,
            tint = tint,
        )
    }
}

@Composable
internal fun AccountSoftHeader(
    title: String,
    icon: Painter,
    modifier: Modifier = Modifier,
    hint: String? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
    ) {
        AccountSoftIcon(painter = icon)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
        ) {
            VitranText(
                text = title,
                style = VitranTextStyle.Title,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (!hint.isNullOrBlank()) {
                VitranText(
                    text = hint,
                    style = VitranTextStyle.Body,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
internal fun AccountTextLink(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    VitranText(
        text = label,
        style = VitranTextStyle.Label,
        color = color,
        modifier = modifier.clickable(role = Role.Button, onClick = onClick),
    )
}
