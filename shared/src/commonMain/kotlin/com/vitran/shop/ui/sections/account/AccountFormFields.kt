package com.vitran.shop.ui.sections.account

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.ic_check
import vitranshop.shared.generated.resources.ic_chevron_down
import vitranshop.shared.generated.resources.ic_lock

@Composable
internal fun AccountStackedField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    supportingText: String? = null,
    supportingPositive: Boolean = false,
    showSupportingCheck: Boolean = false,
    readOnly: Boolean = false,
    trailing: (@Composable () -> Unit)? = null,
) {
    val shape = RoundedCornerShape(VitranRadius.small)
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        VitranText(
            text = label,
            style = VitranTextStyle.Label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(AccountTokens.StackedFieldHeight)
                .clip(shape)
                .border(1.dp, AccountTokens.CardBorder, shape)
                .background(MaterialTheme.colorScheme.surface, shape)
                .padding(horizontal = VitranSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                enabled = !readOnly,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = { inner ->
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
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
                modifier = Modifier.weight(1f),
            )
            if (trailing != null) trailing()
        }
        if (!supportingText.isNullOrBlank()) {
            val supportColor = if (supportingPositive) {
                AccountTokens.Success
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
            ) {
                if (showSupportingCheck && supportingPositive) {
                    VitranIcon(
                        painter = painterResource(Res.drawable.ic_check),
                        contentDescription = null,
                        size = VitranSize.iconSmall,
                        tint = supportColor,
                    )
                }
                VitranText(
                    text = supportingText,
                    style = VitranTextStyle.Label,
                    color = supportColor,
                )
            }
        }
    }
}

@Composable
internal fun AccountDropdownField(
    label: String,
    value: String,
    placeholder: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: Painter = painterResource(Res.drawable.ic_chevron_down),
) {
    var open by remember { mutableStateOf(false) }
    var fieldWidthPx by remember { mutableIntStateOf(0) }
    var fieldHeightPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .onSizeChanged { size ->
                fieldWidthPx = size.width
                fieldHeightPx = size.height
            }
            .clickable(role = Role.Button) { open = true },
    ) {
        AccountStackedField(
            label = label,
            value = value,
            onValueChange = {},
            placeholder = placeholder,
            readOnly = true,
            trailing = {
                AccountTrailingIcon(
                    painter = trailingIcon,
                    contentDescription = null,
                )
            },
        )
        AccountSelectMenu(
            expanded = open,
            onDismiss = { open = false },
            options = options,
            selected = value.ifBlank { placeholder },
            onSelect = onSelect,
            menuWidth = with(density) {
                if (fieldWidthPx > 0) fieldWidthPx.toDp() else 0.dp
            },
            anchorHeightPx = fieldHeightPx,
        )
    }
}

@Composable
internal fun AccountLockedField(
    label: String,
    value: String,
    hint: String,
    changeLabel: String,
    lockedA11y: String,
    onChangeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(VitranRadius.small)
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        VitranText(
            text = label,
            style = VitranTextStyle.Label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(AccountTokens.StackedFieldHeight)
                .clip(shape)
                .border(1.dp, AccountTokens.CardBorder, shape)
                .background(AccountTokens.ImagePlaceholder, shape)
                .padding(horizontal = VitranSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        ) {
            VitranText(
                text = value,
                style = VitranTextStyle.Body,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
                maxLines = 1,
            )
            AccountTrailingIcon(
                painter = painterResource(Res.drawable.ic_lock),
                contentDescription = lockedA11y,
            )
        }
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
        ) {
            VitranText(
                text = hint,
                style = VitranTextStyle.Label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            AccountTextLink(label = changeLabel, onClick = onChangeClick)
        }
    }
}
