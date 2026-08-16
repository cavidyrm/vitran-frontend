package com.vitran.shop.ui.sections.account

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.theme.VitranShapes
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.ic_chevron_down

/**
 * Size / preference accordion — shop.app shows label + chevron when open,
 * with pills directly under the header (no “unset” copy while expanded).
 */
@Composable
internal fun AccountAccordionRow(
    label: String,
    value: String,
    placeholder: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    showDivider: Boolean,
    options: List<String>,
    selected: String?,
    onSelect: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (expanded) AccountTokens.MenuHover else MaterialTheme.colorScheme.surface,
                ),
        ) {
            AccountFieldRow(
                label = label,
                // While open, hide the trailing status — pills carry the selection UI.
                value = if (expanded) "" else value,
                placeholder = if (expanded) "" else placeholder,
                showDivider = false,
                onClick = onToggle,
                trailing = {
                    VitranIcon(
                        painter = painterResource(Res.drawable.ic_chevron_down),
                        contentDescription = null,
                        size = VitranSize.iconSmall,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.graphicsLayer {
                            rotationZ = if (expanded) 180f else 0f
                        },
                    )
                },
            )
        }
        if (expanded) {
            AccountChipRow(
                options = options,
                selected = selected,
                onSelect = onSelect,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AccountTokens.MenuHover)
                    .padding(
                        start = VitranSpacing.lg,
                        end = VitranSpacing.lg,
                        bottom = VitranSpacing.lg,
                    ),
            )
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
internal fun AccountChipRow(
    options: List<String>,
    selected: String?,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    accentOutline: Boolean = false,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        options.forEach { option ->
            val isSelected = option == selected
            val shape = VitranShapes.pill
            val fill = when {
                accentOutline -> MaterialTheme.colorScheme.surface
                isSelected -> MaterialTheme.colorScheme.onSurface
                else -> MaterialTheme.colorScheme.surface
            }
            val border = when {
                accentOutline && isSelected -> MaterialTheme.colorScheme.primary
                accentOutline -> AccountTokens.ChipBorder
                isSelected -> MaterialTheme.colorScheme.onSurface
                else -> AccountTokens.ChipBorder
            }
            val text = when {
                accentOutline -> MaterialTheme.colorScheme.onSurface
                isSelected -> MaterialTheme.colorScheme.surface
                else -> MaterialTheme.colorScheme.onSurface
            }
            Box(
                modifier = Modifier
                    .heightIn(min = 36.dp)
                    .clip(shape)
                    .background(fill, shape)
                    .border(width = 1.dp, color = border, shape = shape)
                    .clickable(role = Role.Button) { onSelect(option) }
                    .padding(horizontal = VitranSpacing.md, vertical = VitranSpacing.sm),
                contentAlignment = Alignment.Center,
            ) {
                VitranText(
                    text = option,
                    style = VitranTextStyle.Label,
                    color = text,
                )
            }
        }
    }
}
