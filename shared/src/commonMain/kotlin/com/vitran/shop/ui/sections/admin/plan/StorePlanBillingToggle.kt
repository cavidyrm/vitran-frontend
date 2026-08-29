package com.vitran.shop.ui.sections.admin.plan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import com.vitran.shop.ui.components.admin.AdminTokens
import com.vitran.shop.ui.sections.account.toPersianDigits
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.store_plan_billing_monthly
import vitranshop.shared.generated.resources.store_plan_billing_yearly
import vitranshop.shared.generated.resources.store_plan_yearly_discount

@Composable
fun StorePlanBillingToggle(
    cycle: StorePlanBillingCycle,
    yearlyDiscountPercent: Int,
    onCycleChange: (StorePlanBillingCycle) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(percent = 50)
    Row(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, StorePlanTokens.CardBorder, shape)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
    ) {
        BillingSegment(
            label = stringResource(Res.string.store_plan_billing_monthly),
            selected = cycle == StorePlanBillingCycle.Monthly,
            onClick = { onCycleChange(StorePlanBillingCycle.Monthly) },
        )
        BillingSegment(
            label = stringResource(Res.string.store_plan_billing_yearly),
            selected = cycle == StorePlanBillingCycle.Yearly,
            onClick = { onCycleChange(StorePlanBillingCycle.Yearly) },
            trailing = {
                val pillShape = RoundedCornerShape(percent = 50)
                Text(
                    text = stringResource(
                        Res.string.store_plan_yearly_discount,
                        toPersianDigits(yearlyDiscountPercent),
                    ),
                    modifier = Modifier
                        .clip(pillShape)
                        .background(StorePlanTokens.DiscountPillBg, pillShape)
                        .padding(horizontal = VitranSpacing.sm, vertical = 2.dp),
                    color = StorePlanTokens.DiscountPillText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                    ),
                )
            },
        )
    }
}

@Composable
private fun BillingSegment(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    trailing: (@Composable () -> Unit)? = null,
) {
    val shape = RoundedCornerShape(percent = 50)
    Row(
        modifier = Modifier
            .clip(shape)
            .background(if (selected) AdminTokens.Brand else MaterialTheme.colorScheme.surface)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = VitranSpacing.lg, vertical = VitranSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        Text(
            text = label,
            color = if (selected) AdminTokens.OnBrand else MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
            ),
        )
        if (trailing != null) {
            Box { trailing() }
        }
    }
}
