package com.vitran.shop.ui.sections.admin.plan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.admin.AdminTokens
import com.vitran.shop.ui.sections.account.toPersianDigits
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.admin_plans_popular_badge
import vitranshop.shared.generated.resources.admin_plans_preview_feature_analytics
import vitranshop.shared.generated.resources.admin_plans_preview_feature_priority
import vitranshop.shared.generated.resources.admin_plans_preview_feature_products
import vitranshop.shared.generated.resources.admin_plans_preview_feature_slots
import vitranshop.shared.generated.resources.admin_plans_preview_monthly
import vitranshop.shared.generated.resources.admin_plans_preview_title
import vitranshop.shared.generated.resources.admin_plans_preview_yearly
import vitranshop.shared.generated.resources.admin_plans_yearly_discount
import vitranshop.shared.generated.resources.ic_check

@Composable
fun AdminPlansPreviewCard(
    plan: AdminPlanDefinition?,
    form: AdminPlanFormState,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val preview = plan?.takeIf { it.id == form.id } ?: form.toDefinition(plan?.icon)
    val discount = form.yearlyDiscountPercent()
        ?: run {
            val full = preview.monthlyPriceToman * 12
            if (preview.yearlyPriceToman in 1 until full) {
                (((full - preview.yearlyPriceToman).toDouble() / full) * 100).toInt()
            } else {
                null
            }
        }
    val bullets = buildList {
        add(
            stringResource(
                Res.string.admin_plans_preview_feature_products,
                productLimitLabel(preview.productLimit),
            ),
        )
        add(
            stringResource(
                Res.string.admin_plans_preview_feature_slots,
                specialSlotsLabel(preview.specialSlots),
            ),
        )
        add(
            stringResource(
                Res.string.admin_plans_preview_feature_analytics,
                preview.analytics.label(),
            ),
        )
        if (preview.prioritySupport) {
            add(stringResource(Res.string.admin_plans_preview_feature_priority))
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(StorePlanTokens.CardRadius))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, StorePlanTokens.CardBorder, RoundedCornerShape(StorePlanTokens.CardRadius))
            .padding(VitranSpacing.lg),
    ) {
        Text(
            text = stringResource(Res.string.admin_plans_preview_title),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        )
        Spacer(modifier = Modifier.height(VitranSpacing.md))
        if (compact) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(StorePlanTokens.CardRadius))
                    .background(AdminPlansTokens.SoftPurple.copy(alpha = 0.45f))
                    .border(
                        1.dp,
                        AdminPlansTokens.PopularBorder.copy(alpha = 0.35f),
                        RoundedCornerShape(StorePlanTokens.CardRadius),
                    )
                    .padding(VitranSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                PreviewIdentity(
                    preview = preview,
                    plan = plan,
                    discount = discount,
                    compact = true,
                )
                Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
                    bullets.forEach { line -> PreviewBullet(line) }
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(StorePlanTokens.CardRadius))
                    .background(AdminPlansTokens.SoftPurple.copy(alpha = 0.45f))
                    .border(
                        1.dp,
                        AdminPlansTokens.PopularBorder.copy(alpha = 0.35f),
                        RoundedCornerShape(StorePlanTokens.CardRadius),
                    )
                    .padding(VitranSpacing.lg),
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1.2f),
                    verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
                ) {
                    bullets.forEach { line -> PreviewBullet(line) }
                }
                PreviewIdentity(
                    preview = preview,
                    plan = plan,
                    discount = discount,
                    compact = false,
                    modifier = Modifier.weight(1f),
                )
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center,
                ) {
                    VitranIcon(
                        painter = painterResource(preview.icon),
                        contentDescription = null,
                        tint = AdminTokens.Brand,
                        modifier = Modifier.size(40.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun PreviewIdentity(
    preview: AdminPlanDefinition,
    plan: AdminPlanDefinition?,
    discount: Int?,
    compact: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (compact) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center,
            ) {
                VitranIcon(
                    painter = painterResource(preview.icon),
                    contentDescription = null,
                    tint = AdminTokens.Brand,
                    modifier = Modifier.size(32.dp),
                )
            }
            Spacer(modifier = Modifier.height(VitranSpacing.sm))
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
        ) {
            Text(
                text = preview.name.ifBlank { "—" },
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = if (compact) 22.sp else 26.sp,
                ),
            )
            if (preview.popular || plan?.popular == true) {
                Text(
                    text = stringResource(Res.string.admin_plans_popular_badge),
                    color = AdminTokens.Brand,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }
        }
        Spacer(modifier = Modifier.height(VitranSpacing.sm))
        Text(
            text = stringResource(
                Res.string.admin_plans_preview_yearly,
                formatTomanAmount(preview.yearlyPriceToman),
            ),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        )
        if (discount != null) {
            Text(
                text = stringResource(
                    Res.string.admin_plans_yearly_discount,
                    toPersianDigits(discount),
                ),
                color = AdminPlansTokens.SuccessText,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(AdminPlansTokens.SuccessSoft)
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            )
        }
        Spacer(modifier = Modifier.height(VitranSpacing.xs))
        Text(
            text = stringResource(
                Res.string.admin_plans_preview_monthly,
                formatTomanAmount(preview.monthlyPriceToman),
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun PreviewBullet(line: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(AdminPlansTokens.SuccessSoft),
            contentAlignment = Alignment.Center,
        ) {
            VitranIcon(
                painter = painterResource(Res.drawable.ic_check),
                contentDescription = null,
                tint = AdminPlansTokens.SuccessText,
                modifier = Modifier.size(14.dp),
            )
        }
        Text(
            text = line,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
