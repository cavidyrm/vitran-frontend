package com.vitran.shop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.vitran.shop.ui.sections.admin.plan.MerchantPlanTopBar
import com.vitran.shop.ui.sections.admin.plan.StorePlanBenefitsAndPayments
import com.vitran.shop.ui.sections.admin.plan.StorePlanCurrentSummaryCard
import com.vitran.shop.ui.sections.admin.plan.StorePlanDashboardTitle
import com.vitran.shop.ui.sections.admin.plan.StorePlanPromoBanner
import com.vitran.shop.ui.sections.admin.plan.StorePlanTokens
import com.vitran.shop.ui.sections.admin.plan.StorePlanUsageSection
import com.vitran.shop.ui.sections.admin.plan.rememberMockStorePlanSubscription
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme

/**
 * Merchant admin — store plan & credit dashboard. Route `/admin/stores/plan`.
 * Mock subscription only; upgrade navigates to the catalog screen.
 */
@Composable
fun StorePlanScreen(
    onBack: () -> Unit,
    onUpgradeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val subscription = rememberMockStorePlanSubscription()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StorePlanTokens.PageBackground),
    ) {
        MerchantPlanTopBar(
            storeName = subscription.storeName,
            onHomeClick = onBack,
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = StorePlanTokens.PageMaxWidth)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        horizontal = StorePlanTokens.PageHorizontal,
                        vertical = VitranSpacing.xxl,
                    ),
                verticalArrangement = Arrangement.spacedBy(StorePlanTokens.SectionGap),
            ) {
                StorePlanDashboardTitle()
                StorePlanCurrentSummaryCard(
                    subscription = subscription,
                    onUpgradeClick = onUpgradeClick,
                )
                StorePlanUsageSection(items = subscription.usage)
                StorePlanBenefitsAndPayments(
                    benefits = subscription.benefits,
                    payments = subscription.payments,
                )
                StorePlanPromoBanner(onViewPlansClick = onUpgradeClick)
            }
        }
    }
}

@Preview
@Composable
private fun StorePlanScreenPreview() {
    VitranTheme {
        StorePlanScreen(onBack = {}, onUpgradeClick = {})
    }
}
