package com.vitran.shop.ui.sections.admin.plan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.admin.AdminTokens
import com.vitran.shop.ui.theme.VitranElevation
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.ic_check
import vitranshop.shared.generated.resources.store_plan_per_month
import vitranshop.shared.generated.resources.store_plan_per_year
import vitranshop.shared.generated.resources.store_plan_recommended_badge
import vitranshop.shared.generated.resources.store_plan_select_cta
import vitranshop.shared.generated.resources.store_plan_toman

@Composable
fun StorePlanPricingCards(
    tiers: List<StorePlanTier>,
    cycle: StorePlanBillingCycle,
    onSelectPlan: (StorePlanTierId) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val desktop = maxWidth >= VitranSize.mdBreakpoint
        if (desktop) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
                verticalAlignment = Alignment.Top,
            ) {
                tiers.forEach { tier ->
                    StorePlanPricingCard(
                        tier = tier,
                        cycle = cycle,
                        onSelect = { onSelectPlan(tier.id) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
            ) {
                tiers.forEach { tier ->
                    StorePlanPricingCard(
                        tier = tier,
                        cycle = cycle,
                        onSelect = { onSelectPlan(tier.id) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun StorePlanPricingCard(
    tier: StorePlanTier,
    cycle: StorePlanBillingCycle,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(StorePlanTokens.CardRadius)
    val borderColor = if (tier.recommended) AdminTokens.Brand else StorePlanTokens.CardBorder
    val borderWidth = if (tier.recommended) 2.dp else 1.dp

    Column(modifier = modifier) {
        if (tier.recommended) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(topStart = VitranRadius.medium, topEnd = VitranRadius.medium))
                    .background(StorePlanTokens.RecommendedBadgeBg)
                    .padding(horizontal = VitranSpacing.lg, vertical = VitranSpacing.xs),
            ) {
                Text(
                    text = stringResource(Res.string.store_plan_recommended_badge),
                    color = AdminTokens.Brand,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                    ),
                )
            }
        } else {
            Spacer(modifier = Modifier.height(24.dp))
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = VitranElevation.medium,
                    shape = shape,
                    clip = false,
                    ambientColor = AdminTokens.CardShadow,
                    spotColor = AdminTokens.CardShadow,
                )
                .clip(shape)
                .background(MaterialTheme.colorScheme.surface, shape)
                .border(borderWidth, borderColor, shape)
                .padding(VitranSpacing.xl),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(VitranRadius.medium))
                    .background(StorePlanTokens.RecommendedBadgeBg),
                contentAlignment = Alignment.Center,
            ) {
                VitranIcon(
                    painter = painterResource(tier.icon),
                    contentDescription = null,
                    size = VitranSize.iconMedium,
                    tint = AdminTokens.Brand,
                )
            }
            Text(
                text = tier.title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                ),
            )
            Text(
                text = tier.tagline,
                color = AdminTokens.Helper,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
            )
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
            ) {
                Text(
                    text = formatTomanAmount(tier.priceFor(cycle)),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                    ),
                )
                Text(
                    text = stringResource(Res.string.store_plan_toman),
                    color = AdminTokens.Helper,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                    modifier = Modifier.padding(bottom = 4.dp),
                )
                Text(
                    text = stringResource(
                        if (cycle == StorePlanBillingCycle.Yearly) {
                            Res.string.store_plan_per_year
                        } else {
                            Res.string.store_plan_per_month
                        },
                    ),
                    color = AdminTokens.Helper,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                    modifier = Modifier.padding(bottom = 4.dp),
                )
            }
            val ctaShape = RoundedCornerShape(VitranRadius.medium)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(ctaShape)
                    .background(if (tier.recommended) AdminTokens.Brand else MaterialTheme.colorScheme.surface)
                    .border(
                        width = 1.dp,
                        color = AdminTokens.Brand,
                        shape = ctaShape,
                    )
                    .clickable(role = Role.Button, onClick = onSelect),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(Res.string.store_plan_select_cta),
                    color = if (tier.recommended) AdminTokens.OnBrand else AdminTokens.Brand,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                    ),
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
                tier.bullets.forEach { bullet ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
                        verticalAlignment = Alignment.Top,
                    ) {
                        VitranIcon(
                            painter = painterResource(Res.drawable.ic_check),
                            contentDescription = null,
                            size = VitranSize.iconSmall,
                            tint = AdminTokens.Brand,
                        )
                        Text(
                            text = bullet.text,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                        )
                    }
                }
            }
        }
    }
}
