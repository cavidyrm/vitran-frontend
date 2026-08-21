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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.vitran.shop.ui.sections.admin.plan.MerchantPlanTopBar
import com.vitran.shop.ui.sections.admin.plan.StorePlanBillingCycle
import com.vitran.shop.ui.sections.admin.plan.StorePlanBillingToggle
import com.vitran.shop.ui.sections.admin.plan.StorePlanComparisonTable
import com.vitran.shop.ui.sections.admin.plan.StorePlanPricingCards
import com.vitran.shop.ui.sections.admin.plan.StorePlanSupportBar
import com.vitran.shop.ui.sections.admin.plan.StorePlanTierId
import com.vitran.shop.ui.sections.admin.plan.StorePlanTokens
import com.vitran.shop.ui.sections.admin.plan.StorePlanUpgradeTitle
import com.vitran.shop.ui.sections.admin.plan.rememberMockStorePlanCatalog
import com.vitran.shop.ui.sections.admin.plan.rememberMockStorePlanSubscription
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme

/**
 * Merchant admin — upgrade store plan. Route `/admin/stores/plan/upgrade`.
 * Mock catalog only; select CTA does not charge.
 */
@Composable
fun StorePlanUpgradeScreen(
    onBack: () -> Unit,
    onSelectPlan: (StorePlanTierId) -> Unit = {},
    onContactSupport: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val catalog = rememberMockStorePlanCatalog()
    val subscription = rememberMockStorePlanSubscription()
    var cycle by remember { mutableStateOf(StorePlanBillingCycle.Monthly) }

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
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                StorePlanUpgradeTitle()
                StorePlanBillingToggle(
                    cycle = cycle,
                    yearlyDiscountPercent = catalog.yearlyDiscountPercent,
                    onCycleChange = { cycle = it },
                )
                StorePlanPricingCards(
                    tiers = catalog.tiers,
                    cycle = cycle,
                    onSelectPlan = onSelectPlan,
                )
                StorePlanComparisonTable(rows = catalog.comparisonRows)
                StorePlanSupportBar(onContactClick = onContactSupport)
            }
        }
    }
}

@Preview
@Composable
private fun StorePlanUpgradeScreenPreview() {
    VitranTheme {
        StorePlanUpgradeScreen(onBack = {})
    }
}
