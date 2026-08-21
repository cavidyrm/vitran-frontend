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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitran.shop.ui.components.VitranIcon
import com.vitran.shop.ui.components.admin.AdminTokens
import com.vitran.shop.ui.sections.account.toPersianDigits
import com.vitran.shop.ui.theme.VitranRadius
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.admin_plans_create
import vitranshop.shared.generated.resources.admin_plans_stat_active_plans
import vitranshop.shared.generated.resources.admin_plans_stat_active_stores
import vitranshop.shared.generated.resources.admin_plans_stat_monthly_revenue
import vitranshop.shared.generated.resources.admin_plans_stat_popular
import vitranshop.shared.generated.resources.admin_plans_subtitle
import vitranshop.shared.generated.resources.admin_plans_title
import vitranshop.shared.generated.resources.ic_chart
import vitranshop.shared.generated.resources.ic_people
import vitranshop.shared.generated.resources.ic_plus
import vitranshop.shared.generated.resources.ic_star_outline
import vitranshop.shared.generated.resources.ic_wallet

@Composable
fun AdminPlansPageHeader(
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val createButton: @Composable () -> Unit = {
        Row(
            modifier = Modifier
                .then(if (compact) Modifier.fillMaxWidth() else Modifier)
                .clip(RoundedCornerShape(VitranRadius.medium))
                .background(AdminTokens.Brand)
                .clickable(role = Role.Button, onClick = onCreateClick)
                .padding(horizontal = VitranSpacing.lg, vertical = VitranSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm, Alignment.CenterHorizontally),
        ) {
            VitranIcon(
                painter = painterResource(Res.drawable.ic_plus),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = stringResource(Res.string.admin_plans_create),
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            )
        }
    }

    if (compact) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
        ) {
            Column {
                Text(
                    text = stringResource(Res.string.admin_plans_title),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                    ),
                )
                Spacer(modifier = Modifier.height(VitranSpacing.xs))
                Text(
                    text = stringResource(Res.string.admin_plans_subtitle),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            createButton()
        }
    } else {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(Res.string.admin_plans_title),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                    ),
                )
                Spacer(modifier = Modifier.height(VitranSpacing.xs))
                Text(
                    text = stringResource(Res.string.admin_plans_subtitle),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Spacer(modifier = Modifier.width(VitranSpacing.md))
            createButton()
        }
    }
}

@Composable
fun AdminPlansStatsRow(
    stats: AdminPlansStats,
    modifier: Modifier = Modifier,
) {
    val cards = listOf(
        StatCardModel(
            title = stringResource(Res.string.admin_plans_stat_active_plans),
            value = toPersianDigits(stats.activePlans),
            icon = Res.drawable.ic_chart,
        ),
        StatCardModel(
            title = stringResource(Res.string.admin_plans_stat_popular),
            value = stats.popularPlanName,
            icon = Res.drawable.ic_star_outline,
        ),
        StatCardModel(
            title = stringResource(Res.string.admin_plans_stat_active_stores),
            value = toPersianDigits(stats.activeStores),
            icon = Res.drawable.ic_people,
        ),
        StatCardModel(
            title = stringResource(Res.string.admin_plans_stat_monthly_revenue),
            value = "${formatTomanAmount(stats.monthlyRevenueToman)} تومان",
            icon = Res.drawable.ic_wallet,
        ),
    )
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        when {
            maxWidth >= VitranSize.desktopBreakpoint -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
                ) {
                    cards.forEach { card ->
                        AdminPlansStatCard(model = card, modifier = Modifier.weight(1f))
                    }
                }
            }
            maxWidth >= VitranSize.mdBreakpoint -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
                ) {
                    cards.chunked(2).forEach { rowCards ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
                        ) {
                            rowCards.forEach { card ->
                                AdminPlansStatCard(model = card, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
            else -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
                ) {
                    cards.chunked(2).forEach { rowCards ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
                        ) {
                            rowCards.forEach { card ->
                                AdminPlansStatCard(
                                    model = card,
                                    modifier = Modifier.weight(1f),
                                    compact = true,
                                )
                            }
                            if (rowCards.size == 1) {
                                Box(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Immutable
private data class StatCardModel(
    val title: String,
    val value: String,
    val icon: DrawableResource,
)

@Composable
private fun AdminPlansStatCard(
    model: StatCardModel,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(StorePlanTokens.CardRadius))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, StorePlanTokens.CardBorder, RoundedCornerShape(StorePlanTokens.CardRadius))
            .padding(if (compact) VitranSpacing.md else VitranSpacing.lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(if (compact) VitranSpacing.sm else VitranSpacing.md),
    ) {
        Box(
            modifier = Modifier
                .size(if (compact) 36.dp else 44.dp)
                .clip(CircleShape)
                .background(AdminPlansTokens.SoftPurple),
            contentAlignment = Alignment.Center,
        ) {
            VitranIcon(
                painter = painterResource(model.icon),
                contentDescription = null,
                tint = AdminTokens.Brand,
                modifier = Modifier.size(if (compact) 18.dp else 22.dp),
            )
        }
        Column(modifier = Modifier.weight(1f, fill = false)) {
            Text(
                text = model.title,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = model.value,
                color = MaterialTheme.colorScheme.onSurface,
                style = (if (compact) {
                    MaterialTheme.typography.titleSmall
                } else {
                    MaterialTheme.typography.titleMedium
                }).copy(fontWeight = FontWeight.Bold),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
