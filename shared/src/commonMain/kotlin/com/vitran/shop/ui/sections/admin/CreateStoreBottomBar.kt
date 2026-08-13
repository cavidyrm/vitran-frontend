package com.vitran.shop.ui.sections.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.admin.AdminTokens
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.admin_create_store_back
import vitranshop.shared.generated.resources.admin_create_store_next
import vitranshop.shared.generated.resources.admin_create_store_publish
import vitranshop.shared.generated.resources.admin_create_store_save
import vitranshop.shared.generated.resources.admin_last_saved
import vitranshop.shared.generated.resources.admin_publish_done

@Composable
fun CreateStoreBottomBar(
    step: CreateStoreStep,
    brandColor: Color,
    onBrandColor: Color,
    lastSavedLabel: String?,
    canPublish: Boolean,
    published: Boolean,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onSaveDraft: () -> Unit,
    onPublish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isFirst = step == CreateStoreStep.Basics
    val isLast = step == CreateStoreStep.Publish
    Row(
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
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        if (lastSavedLabel != null) {
            Text(
                text = stringResource(Res.string.admin_last_saved, lastSavedLabel),
                color = AdminTokens.Helper,
                fontSize = 12.sp,
            )
        } else {
            Box {}
        }
        Row(horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
            if (!isFirst) {
                AdminBarButton(
                    label = stringResource(Res.string.admin_create_store_back),
                    emphasized = false,
                    brandColor = brandColor,
                    onBrandColor = onBrandColor,
                    onClick = onBack,
                )
            }
            AdminBarButton(
                label = stringResource(Res.string.admin_create_store_save),
                emphasized = false,
                brandColor = brandColor,
                onBrandColor = onBrandColor,
                onClick = onSaveDraft,
            )
            if (isLast) {
                AdminBarButton(
                    label = stringResource(
                        if (published) Res.string.admin_publish_done else Res.string.admin_create_store_publish,
                    ),
                    emphasized = true,
                    enabled = canPublish && !published,
                    brandColor = brandColor,
                    onBrandColor = onBrandColor,
                    prominent = true,
                    onClick = onPublish,
                )
            } else {
                AdminBarButton(
                    label = stringResource(Res.string.admin_create_store_next),
                    emphasized = true,
                    brandColor = brandColor,
                    onBrandColor = onBrandColor,
                    onClick = onNext,
                )
            }
        }
    }
}

@Composable
private fun AdminBarButton(
    label: String,
    emphasized: Boolean,
    brandColor: Color,
    onBrandColor: Color,
    onClick: () -> Unit,
    enabled: Boolean = true,
    prominent: Boolean = false,
) {
    val shape = RoundedCornerShape(VitranRadius.small)
    Box(
        modifier = Modifier
            .height(if (prominent) 48.dp else AdminTokens.SaveHeight)
            .clip(shape)
            .then(
                if (emphasized) {
                    Modifier.background(
                        if (enabled) brandColor else AdminTokens.FieldBorder,
                    )
                } else {
                    Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, AdminTokens.FieldBorder, shape)
                },
            )
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = if (prominent) VitranSpacing.xl else VitranSpacing.lg),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = if (emphasized) {
                if (enabled) onBrandColor else MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            fontWeight = if (prominent) FontWeight.SemiBold else FontWeight.Medium,
            fontSize = if (prominent) 14.sp else 13.sp,
        )
    }
}
