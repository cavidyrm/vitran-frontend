package com.vitran.shop.ui.sections.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.admin.AdminPrimaryButton
import com.vitran.shop.ui.components.admin.AdminSecondaryButton
import com.vitran.shop.ui.components.admin.AdminTextButton
import com.vitran.shop.ui.components.admin.AdminTokens
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.admin_create_product_cancel
import vitranshop.shared.generated.resources.admin_create_product_more_a11y
import vitranshop.shared.generated.resources.admin_create_product_save_draft
import vitranshop.shared.generated.resources.admin_create_product_save_publish
import vitranshop.shared.generated.resources.admin_create_product_saved
import vitranshop.shared.generated.resources.admin_create_product_saving
import vitranshop.shared.generated.resources.admin_create_product_unsaved
import vitranshop.shared.generated.resources.ic_more_horiz

@Composable
fun CreateProductBottomBar(
    state: CreateProductFormState,
    compact: Boolean,
    onCancel: () -> Unit,
    onSaveDraft: () -> Unit,
    onSavePublish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val saving = state.savePhase == ProductSavePhase.Saving
    val saved = state.savePhase == ProductSavePhase.Saved
    val draftLabel = when {
        saving -> stringResource(Res.string.admin_create_product_saving)
        saved -> stringResource(Res.string.admin_create_product_saved)
        else -> stringResource(Res.string.admin_create_product_save_draft)
    }
    val publishLabel = when {
        saving -> stringResource(Res.string.admin_create_product_saving)
        saved -> stringResource(Res.string.admin_create_product_saved)
        else -> stringResource(Res.string.admin_create_product_save_publish)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    color = AdminTokens.CardBorder,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx(),
                )
            }
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = VitranSpacing.xl, vertical = VitranSpacing.md),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        if (compact) {
            if (state.dirty && !saved) {
                Text(
                    text = stringResource(Res.string.admin_create_product_unsaved),
                    color = AdminTokens.Helper,
                    fontSize = 13.sp,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
            ) {
                OverflowActions(
                    enabled = !saving,
                    onCancel = onCancel,
                    onSaveDraft = onSaveDraft,
                )
                AdminPrimaryButton(
                    label = publishLabel,
                    onClick = onSavePublish,
                    enabled = !saving,
                    fillMaxWidth = true,
                    modifier = Modifier.weight(1f),
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
                ) {
                    AdminPrimaryButton(
                        label = draftLabel,
                        onClick = onSaveDraft,
                        enabled = !saving,
                    )
                    AdminSecondaryButton(
                        label = publishLabel,
                        onClick = onSavePublish,
                        enabled = !saving,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
                ) {
                    if (state.dirty && !saved) {
                        Text(
                            text = stringResource(Res.string.admin_create_product_unsaved),
                            color = AdminTokens.Helper,
                            fontSize = 13.sp,
                        )
                    }
                    AdminTextButton(
                        label = stringResource(Res.string.admin_create_product_cancel),
                        onClick = onCancel,
                    )
                }
            }
        }
    }
}

@Composable
private fun OverflowActions(
    enabled: Boolean,
    onCancel: () -> Unit,
    onSaveDraft: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Box(
            modifier = Modifier
                .size(VitranSize.touchTarget)
                .clip(RoundedCornerShape(VitranRadius.small))
                .border(1.dp, AdminTokens.FieldBorder, RoundedCornerShape(VitranRadius.small))
                .clickable(enabled = enabled, role = Role.Button) { expanded = true },
            contentAlignment = Alignment.Center,
        ) {
            VitranIcon(
                painter = painterResource(Res.drawable.ic_more_horiz),
                contentDescription = stringResource(Res.string.admin_create_product_more_a11y),
                size = VitranSize.iconMedium,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
        if (expanded) {
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(0, 0),
                onDismissRequest = { expanded = false },
                properties = PopupProperties(focusable = true, dismissOnClickOutside = true),
            ) {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(AdminTokens.FieldRadius))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, AdminTokens.CardBorder, RoundedCornerShape(AdminTokens.FieldRadius))
                        .padding(vertical = VitranSpacing.xs),
                ) {
                    AdminTextButton(
                        label = stringResource(Res.string.admin_create_product_cancel),
                        onClick = {
                            expanded = false
                            onCancel()
                        },
                    )
                    AdminTextButton(
                        label = stringResource(Res.string.admin_create_product_save_draft),
                        onClick = {
                            expanded = false
                            onSaveDraft()
                        },
                    )
                }
            }
        }
    }
}
