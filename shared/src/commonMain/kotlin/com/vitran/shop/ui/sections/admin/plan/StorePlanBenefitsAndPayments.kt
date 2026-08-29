package com.vitran.shop.ui.sections.admin.plan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
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
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.ic_check
import vitranshop.shared.generated.resources.store_plan_benefits_title
import vitranshop.shared.generated.resources.store_plan_payment_failed
import vitranshop.shared.generated.resources.store_plan_payment_paid
import vitranshop.shared.generated.resources.store_plan_payments_title

@Composable
fun StorePlanBenefitsAndPayments(
    benefits: List<String>,
    payments: List<StorePlanPaymentEntry>,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val desktop = maxWidth >= VitranSize.mdBreakpoint
        if (desktop) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
            ) {
                BenefitsCard(benefits = benefits, modifier = Modifier.weight(1f))
                PaymentsCard(payments = payments, modifier = Modifier.weight(1f))
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
            ) {
                BenefitsCard(benefits = benefits, modifier = Modifier.fillMaxWidth())
                PaymentsCard(payments = payments, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun BenefitsCard(
    benefits: List<String>,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(StorePlanTokens.CardRadius)
    Column(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(1.dp, StorePlanTokens.CardBorder, shape)
            .padding(VitranSpacing.xl),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
    ) {
        Text(
            text = stringResource(Res.string.store_plan_benefits_title),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            ),
        )
        Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.md)) {
            benefits.forEach { benefit ->
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
                        text = benefit,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentsCard(
    payments: List<StorePlanPaymentEntry>,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(StorePlanTokens.CardRadius)
    Column(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(1.dp, StorePlanTokens.CardBorder, shape)
            .padding(VitranSpacing.xl),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
    ) {
        Text(
            text = stringResource(Res.string.store_plan_payments_title),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            ),
        )
        Column {
            payments.forEachIndexed { index, entry ->
                if (index > 0) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = VitranSpacing.sm),
                        color = StorePlanTokens.CardBorder,
                    )
                }
                PaymentRow(entry = entry)
            }
        }
    }
}

@Composable
private fun PaymentRow(entry: StorePlanPaymentEntry) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = entry.dateLabel,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                ),
            )
            Text(
                text = entry.amountLabel,
                color = AdminTokens.Helper,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
            )
        }
        val paid = entry.status == StorePlanPaymentStatus.Paid
        val pill = RoundedCornerShape(percent = 50)
        Text(
            text = stringResource(
                if (paid) Res.string.store_plan_payment_paid else Res.string.store_plan_payment_failed,
            ),
            modifier = Modifier
                .clip(pill)
                .background(
                    if (paid) StorePlanTokens.PaidBadgeBg else StorePlanTokens.FailedBadgeBg,
                    pill,
                )
                .padding(horizontal = VitranSpacing.sm, vertical = 4.dp),
            color = if (paid) StorePlanTokens.PaidBadgeText else StorePlanTokens.FailedBadgeText,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
            ),
        )
    }
}
