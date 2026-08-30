package com.vitran.shop.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitran.shop.core.platform.share.ShareManager
import com.vitran.shop.di.vitranKoinViewModel
import com.vitran.shop.feature.seller.referral.domain.model.ReferralCreditId
import com.vitran.shop.feature.seller.referral.presentation.ReferralsContentState
import com.vitran.shop.feature.seller.referral.presentation.ReferralsUiEffect
import com.vitran.shop.feature.seller.referral.presentation.ReferralsViewModel
import com.vitran.shop.ui.components.SiteFooterLinkId
import com.vitran.shop.ui.components.admin.AdminPrimaryButton
import com.vitran.shop.ui.sections.account.AccountDest
import com.vitran.shop.ui.sections.account.AccountPageShell
import com.vitran.shop.ui.sections.account.AccountTokens
import com.vitran.shop.ui.sections.account.ProfileReferralCard
import com.vitran.shop.ui.sections.account.ProfileReferralHistory
import com.vitran.shop.ui.sections.account.mapReferralProfileToUi
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_nav_referrals

@Composable
fun ReferralsScreen(
    onBack: () -> Unit,
    onDestClick: (AccountDest) -> Unit,
    onOpenSaved: () -> Unit,
    onFooterLinkClick: (SiteFooterLinkId) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ReferralsViewModel = vitranKoinViewModel(),
    shareManager: ShareManager = koinInject(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is ReferralsUiEffect.ShareInvite -> {
                    shareManager.share(
                        text = "با کد ${effect.code} ثبت‌نام کنید",
                        url = effect.inviteUrl,
                    )
                }
            }
        }
    }

    AccountPageShell(
        dest = AccountDest.Referrals,
        onDestClick = onDestClick,
        onSavedClick = onOpenSaved,
        onBack = onBack,
        backTitle = stringResource(Res.string.account_nav_referrals),
        contentMaxWidth = AccountTokens.ReferralsContentMaxWidth,
        onFooterLinkClick = onFooterLinkClick,
        modifier = modifier,
    ) {
        when (val content = state.content) {
            ReferralsContentState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(VitranSpacing.xxl),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
            is ReferralsContentState.Error -> {
                Column(
                    modifier = Modifier.padding(VitranSpacing.xl),
                    verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(content.error.message ?: "خطا در دریافت دعوت‌ها")
                    AdminPrimaryButton(label = "تلاش دوباره", onClick = { viewModel.load() })
                }
            }
            is ReferralsContentState.Content -> {
                val ui = mapReferralProfileToUi(content.profile)
                ProfileReferralCard(
                    referral = ui,
                    onShareClick = { viewModel.shareInvite() },
                )
                if (state.shops.size > 1) {
                    Text(
                        text = "فروشگاه برای اعمال اعتبار: ${state.shops.firstOrNull { it.id == state.selectedShopId }?.title.orEmpty()}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = VitranSpacing.lg),
                    )
                }
                state.applyError?.let {
                    Text(
                        text = it.message ?: "خطا در اعمال اعتبار",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = VitranSpacing.lg),
                    )
                }
                state.applySuccessMessage?.let {
                    Text(
                        text = it,
                        modifier = Modifier.padding(horizontal = VitranSpacing.lg),
                    )
                }
                ProfileReferralHistory(
                    referral = ui,
                    onInviteClick = { viewModel.shareInvite() },
                    onApplyCredit = { creditId ->
                        creditId.toLongOrNull()?.let {
                            viewModel.applyCredit(ReferralCreditId(it))
                        }
                    },
                    applyingCreditId = state.applyingCreditId?.value?.toString(),
                )
            }
        }
    }
}
