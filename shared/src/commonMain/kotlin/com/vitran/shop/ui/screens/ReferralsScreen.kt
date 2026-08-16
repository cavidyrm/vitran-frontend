package com.vitran.shop.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vitran.shop.ui.sections.account.AccountDest
import com.vitran.shop.ui.sections.account.AccountPageShell
import com.vitran.shop.ui.sections.account.AccountTokens
import com.vitran.shop.ui.sections.account.ProfileReferralCard
import com.vitran.shop.ui.sections.account.ProfileReferralHistory
import com.vitran.shop.ui.sections.account.rememberMockReferralProfile
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_nav_referrals

@Composable
fun ReferralsScreen(
    onBack: () -> Unit,
    onDestClick: (AccountDest) -> Unit,
    onOpenSaved: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val referral = rememberMockReferralProfile()
    AccountPageShell(
        dest = AccountDest.Referrals,
        onDestClick = onDestClick,
        onSavedClick = onOpenSaved,
        onBack = onBack,
        backTitle = stringResource(Res.string.account_nav_referrals),
        contentMaxWidth = AccountTokens.ReferralsContentMaxWidth,
        modifier = modifier,
    ) {
        ProfileReferralCard(referral = referral)
        ProfileReferralHistory(referral = referral)
    }
}
