package com.vitran.shop.ui.sections.admin.plan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.admin.AdminTokens
import com.vitran.shop.ui.sections.account.toPersianDigits
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.store_plan_usage_section_title

@Composable
fun StorePlanUsageSection(
    items: List<StorePlanUsageItem>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
    ) {
        Text(
            text = stringResource(Res.string.store_plan_usage_section_title),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
            ),
        )
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val desktop = maxWidth >= VitranSize.mdBreakpoint
            if (desktop) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
                ) {
                    items.forEach { item ->
                        UsageCard(item = item, modifier = Modifier.weight(1f))
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
                ) {
                    items.chunked(2).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
                        ) {
                            rowItems.forEach { item ->
                                UsageCard(item = item, modifier = Modifier.weight(1f))
                            }
                            if (rowItems.size == 1) {
                                Box(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UsageCard(
    item: StorePlanUsageItem,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(StorePlanTokens.CardRadius)
    Column(
        modifier = modifier
            .height(StorePlanTokens.UsageCardMinHeight)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(1.dp, StorePlanTokens.CardBorder, shape)
            .padding(VitranSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(VitranRadius.medium))
                    .background(StorePlanTokens.RecommendedBadgeBg),
                contentAlignment = Alignment.Center,
            ) {
                VitranIcon(
                    painter = painterResource(item.icon),
                    contentDescription = null,
                    size = VitranSize.iconSmall,
                    tint = AdminTokens.Brand,
                )
            }
            Text(
                text = item.title,
                color = AdminTokens.Helper,
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp),
            )
        }
        Text(
            text = item.valueLabel,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            ),
        )
        when (item.kind) {
            StorePlanUsageKind.Meter -> {
                val progress = item.progress?.coerceIn(0f, 1f) ?: 0f
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(percent = 50)),
                    color = AdminTokens.Brand,
                    trackColor = StorePlanTokens.ProgressTrack,
                )
                Text(
                    text = toPersianDigits("${(progress * 100).toInt()}٪"),
                    color = AdminTokens.Helper,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                )
            }
            StorePlanUsageKind.Status -> {
                val status = item.statusLabel.orEmpty()
                if (status.isNotBlank()) {
                    val pill = RoundedCornerShape(percent = 50)
                    Text(
                        text = status,
                        modifier = Modifier
                            .clip(pill)
                            .background(StorePlanTokens.ActiveBadgeBg, pill)
                            .padding(horizontal = VitranSpacing.sm, vertical = 2.dp),
                        color = StorePlanTokens.ActiveBadgeText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                        ),
                    )
                }
            }
        }
    }
}
