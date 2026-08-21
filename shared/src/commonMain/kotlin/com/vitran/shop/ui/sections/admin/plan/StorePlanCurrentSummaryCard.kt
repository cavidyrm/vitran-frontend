package com.vitran.shop.ui.sections.admin.plan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.admin.AdminTokens
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.ic_arrow_up
import vitranshop.shared.generated.resources.ic_star_outline
import vitranshop.shared.generated.resources.store_plan_current_label
import vitranshop.shared.generated.resources.store_plan_upgrade_cta

@Composable
fun StorePlanCurrentSummaryCard(
    subscription: StorePlanSubscription,
    onUpgradeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(StorePlanTokens.CardRadius)
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(1.dp, StorePlanTokens.SoftBorder, shape)
            .padding(VitranSpacing.xl),
    ) {
        val desktop = maxWidth >= VitranSize.mdBreakpoint
        if (desktop) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xl),
            ) {
                CurrentPlanIdentity(
                    subscription = subscription,
                    modifier = Modifier.weight(1f),
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
                ) {
                    Text(
                        text = subscription.priceLabel,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                        ),
                    )
                    Text(
                        text = subscription.renewalLabel,
                        color = AdminTokens.Helper,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                    )
                }
                UpgradeButton(onClick = onUpgradeClick)
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
            ) {
                CurrentPlanIdentity(subscription = subscription)
                Text(
                    text = subscription.priceLabel,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                    ),
                )
                Text(
                    text = subscription.renewalLabel,
                    color = AdminTokens.Helper,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                )
                UpgradeButton(
                    onClick = onUpgradeClick,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun CurrentPlanIdentity(
    subscription: StorePlanSubscription,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        Text(
            text = stringResource(Res.string.store_plan_current_label),
            color = AdminTokens.Helper,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
        )
        val badgeShape = RoundedCornerShape(percent = 50)
        Row(
            modifier = Modifier
                .clip(badgeShape)
                .background(StorePlanTokens.RecommendedBadgeBg, badgeShape)
                .padding(horizontal = VitranSpacing.md, vertical = VitranSpacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
        ) {
            VitranIcon(
                painter = painterResource(Res.drawable.ic_star_outline),
                contentDescription = null,
                size = VitranSize.iconSmall,
                tint = AdminTokens.Brand,
            )
            Text(
                text = subscription.tierTitle,
                color = AdminTokens.Brand,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                ),
            )
        }
    }
}

@Composable
private fun UpgradeButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(VitranRadius.medium)
    Row(
        modifier = modifier
            .height(44.dp)
            .clip(shape)
            .background(AdminTokens.Brand, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = VitranSpacing.xl),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        VitranIcon(
            painter = painterResource(Res.drawable.ic_arrow_up),
            contentDescription = null,
            size = VitranSize.iconSmall,
            tint = AdminTokens.OnBrand,
        )
        Text(
            text = stringResource(Res.string.store_plan_upgrade_cta),
            modifier = Modifier.padding(start = VitranSpacing.sm),
            color = AdminTokens.OnBrand,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
            ),
        )
    }
}
