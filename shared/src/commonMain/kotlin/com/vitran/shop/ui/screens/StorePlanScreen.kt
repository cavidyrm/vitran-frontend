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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitran.shop.di.vitranKoinViewModel
import com.vitran.shop.feature.seller.subscription.presentation.StorePlanContentState
import com.vitran.shop.feature.seller.subscription.presentation.StorePlanViewModel
import com.vitran.shop.ui.components.admin.AdminPrimaryButton
import com.vitran.shop.ui.sections.admin.plan.MerchantPlanTopBar
import com.vitran.shop.ui.sections.admin.plan.StorePlanBenefitsAndPayments
import com.vitran.shop.ui.sections.admin.plan.StorePlanCurrentSummaryCard
import com.vitran.shop.ui.sections.admin.plan.StorePlanDashboardTitle
import com.vitran.shop.ui.sections.admin.plan.StorePlanPromoBanner
import com.vitran.shop.ui.sections.admin.plan.StorePlanTokens
import com.vitran.shop.ui.sections.admin.plan.StorePlanUsageSection
import com.vitran.shop.ui.sections.admin.plan.mapShopSubscriptionToUi
import com.vitran.shop.ui.sections.admin.plan.rememberMockStorePlanSubscription
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme

/**
 * Merchant admin — store plan & credit dashboard. Route `/admin/stores/plan`.
 * Wired to [StorePlanViewModel] / SubscriptionRepository (Phase 9).
 */
@Composable
fun StorePlanScreen(
    onBack: () -> Unit,
    onUpgradeClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StorePlanViewModel = vitranKoinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StorePlanTokens.PageBackground),
    ) {
        val storeName =
            when (val c = state.content) {
                is StorePlanContentState.Content -> c.storeName
                else -> ""
            }
        MerchantPlanTopBar(
            storeName = storeName.ifBlank { "فروشگاه" },
            onHomeClick = onBack,
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.TopCenter,
        ) {
            when (val content = state.content) {
                StorePlanContentState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }
                is StorePlanContentState.Error -> {
                    Column(
                        modifier = Modifier.padding(VitranSpacing.xxl),
                        verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = content.error.message ?: "خطا در دریافت اشتراک",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        AdminPrimaryButton(label = "تلاش دوباره", onClick = { viewModel.load() })
                    }
                }
                is StorePlanContentState.Content -> {
                    val subscription =
                        mapShopSubscriptionToUi(
                            content.subscription,
                            content.entitlements,
                            content.storeName,
                        )
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
                        // Payments list empty — no billing-history API (Gap).
                        StorePlanBenefitsAndPayments(
                            benefits = subscription.benefits,
                            payments = emptyList(),
                        )
                        StorePlanPromoBanner(onViewPlansClick = onUpgradeClick)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun StorePlanScreenPreview() {
    VitranTheme {
        val subscription = rememberMockStorePlanSubscription()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(StorePlanTokens.PageBackground)
                .padding(VitranSpacing.xxl),
            verticalArrangement = Arrangement.spacedBy(StorePlanTokens.SectionGap),
        ) {
            StorePlanDashboardTitle()
            StorePlanCurrentSummaryCard(subscription = subscription, onUpgradeClick = {})
        }
    }
}
