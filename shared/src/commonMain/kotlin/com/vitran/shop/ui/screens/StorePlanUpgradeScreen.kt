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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitran.shop.core.platform.share.ExternalUrlLauncher
import com.vitran.shop.di.vitranKoinViewModel
import com.vitran.shop.feature.seller.plan.domain.model.PlanId
import com.vitran.shop.feature.seller.subscription.domain.model.PaymentFlowState
import com.vitran.shop.feature.seller.subscription.presentation.StorePlanUpgradeUiEffect
import com.vitran.shop.feature.seller.subscription.presentation.StorePlanUpgradeViewModel
import com.vitran.shop.ui.components.admin.AdminPrimaryButton
import com.vitran.shop.ui.components.admin.AdminSecondaryButton
import com.vitran.shop.ui.sections.admin.plan.MerchantPlanTopBar
import com.vitran.shop.ui.sections.admin.plan.StorePlanBillingCycle
import com.vitran.shop.ui.sections.admin.plan.StorePlanComparisonTable
import com.vitran.shop.ui.sections.admin.plan.StorePlanPricingCards
import com.vitran.shop.ui.sections.admin.plan.StorePlanSupportBar
import com.vitran.shop.ui.sections.admin.plan.StorePlanTokens
import com.vitran.shop.ui.sections.admin.plan.StorePlanUpgradeTitle
import com.vitran.shop.ui.sections.admin.plan.mapPlansToCatalog
import com.vitran.shop.ui.sections.admin.plan.rememberMockStorePlanCatalog
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.theme.VitranTheme
import org.koin.compose.koinInject

/**
 * Merchant admin — upgrade store plan. Route `/admin/stores/plan/upgrade`.
 * Wired to plan catalog + purchase handoff (Phase 9). Yearly toggle removed (no annual API).
 */
@Composable
fun StorePlanUpgradeScreen(
    onBack: () -> Unit,
    onContactSupport: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: StorePlanUpgradeViewModel = vitranKoinViewModel(),
    externalUrlLauncher: ExternalUrlLauncher = koinInject(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is StorePlanUpgradeUiEffect.OpenExternalUrl -> {
                    val opened = externalUrlLauncher.open(effect.url)
                    if (!opened) viewModel.onPaymentUrlLaunchFailed()
                }
            }
        }
    }

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    viewModel.onAppResumed()
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StorePlanTokens.PageBackground),
    ) {
        MerchantPlanTopBar(
            storeName = state.storeName.ifBlank { "فروشگاه" },
            onHomeClick = onBack,
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.TopCenter,
        ) {
            when {
                state.shopsLoading || state.plansLoading -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }
                state.shopsError != null -> {
                    Column(
                        modifier = Modifier.padding(VitranSpacing.xxl),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
                    ) {
                        Text(state.shopsError?.message ?: "خطا")
                        AdminPrimaryButton(label = "تلاش دوباره", onClick = { viewModel.load() })
                    }
                }
                state.plansError != null && state.plans.isEmpty() -> {
                    Column(
                        modifier = Modifier.padding(VitranSpacing.xxl),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
                    ) {
                        Text(state.plansError?.message ?: "خطا در دریافت پلن‌ها")
                        AdminPrimaryButton(label = "تلاش دوباره", onClick = { viewModel.load() })
                    }
                }
                else -> {
                    val catalog =
                        mapPlansToCatalog(
                            plans = state.plans,
                            currentPlanId = state.currentSubscription?.plan?.id?.value,
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
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        StorePlanUpgradeTitle()
                        PaymentStatusBanner(
                            payment = state.payment,
                            onCheckPayment = { viewModel.verifyPendingPayment() },
                            onRetryOpen = { viewModel.retryOpenPaymentUrl() },
                            onDismiss = { viewModel.clearPaymentState() },
                        )
                        StorePlanPricingCards(
                            tiers = catalog.tiers.filter { it.planId != 0L && !state.plans.any { p -> p.id.value == it.planId && p.slug.isFree } },
                            cycle = StorePlanBillingCycle.Monthly,
                            onSelectPlan = { planId ->
                                if (planId != 0L) {
                                    viewModel.purchasePlan(PlanId(planId))
                                }
                            },
                            purchasingPlanId = state.purchasingPlanId?.value,
                        )
                        if (catalog.comparisonRows.isNotEmpty()) {
                            StorePlanComparisonTable(rows = catalog.comparisonRows)
                        }
                        StorePlanSupportBar(onContactClick = onContactSupport)
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentStatusBanner(
    payment: PaymentFlowState,
    onCheckPayment: () -> Unit,
    onRetryOpen: () -> Unit,
    onDismiss: () -> Unit,
) {
    when (payment) {
        PaymentFlowState.Idle, PaymentFlowState.Initiating -> Unit
        is PaymentFlowState.AwaitingExternalPayment -> {
            Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
                Text(
                    "پرداخت در مرورگر باز شد. پس از اتمام، وضعیت را بررسی کنید.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                AdminPrimaryButton(label = "بررسی پرداخت", onClick = onCheckPayment)
            }
        }
        is PaymentFlowState.Verifying -> {
            Text("در حال بررسی اشتراک…", style = MaterialTheme.typography.bodyMedium)
        }
        is PaymentFlowState.Confirmed -> {
            Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
                Text(
                    "پرداخت تأیید شد. پلن ${payment.subscription.plan.title} فعال است.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                AdminSecondaryButton(label = "باشه", onClick = onDismiss)
            }
        }
        is PaymentFlowState.NotYetConfirmed -> {
            Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
                Text(
                    "هنوز اشتراک جدید تأیید نشده است. دوباره بررسی کنید.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                AdminPrimaryButton(label = "بررسی دوباره", onClick = onCheckPayment)
            }
        }
        is PaymentFlowState.Error -> {
            Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm)) {
                Text(payment.message ?: "خطا", style = MaterialTheme.typography.bodyMedium)
                if (payment.session != null) {
                    AdminPrimaryButton(label = "باز کردن دوباره پرداخت", onClick = onRetryOpen)
                }
            }
        }
    }
}

@Preview
@Composable
private fun StorePlanUpgradeScreenPreview() {
    VitranTheme {
        val catalog = rememberMockStorePlanCatalog()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(StorePlanTokens.PageBackground)
                .padding(VitranSpacing.xxl),
            verticalArrangement = Arrangement.spacedBy(StorePlanTokens.SectionGap),
        ) {
            StorePlanUpgradeTitle()
            StorePlanPricingCards(
                tiers = catalog.tiers,
                cycle = StorePlanBillingCycle.Monthly,
                onSelectPlan = {},
            )
        }
    }
}
