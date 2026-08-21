package com.vitran.shop.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.sections.account.AccountCard
import com.vitran.shop.ui.components.SiteFooterLinkId
import com.vitran.shop.ui.sections.account.AccountDest
import com.vitran.shop.ui.sections.account.AccountPageShell
import com.vitran.shop.ui.sections.account.AccountPrimaryButton
import com.vitran.shop.ui.sections.account.AccountTokens
import com.vitran.shop.ui.sections.account.cities.AccountCity
import com.vitran.shop.ui.sections.account.cities.AccountCityDetailHeader
import com.vitran.shop.ui.sections.account.cities.AccountCityForm
import com.vitran.shop.ui.sections.account.cities.AccountCityFormMode
import com.vitran.shop.ui.sections.account.cities.MockAccountCities
import com.vitran.shop.ui.sections.account.cities.findMockAccountCity
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_city_detail_back_list
import vitranshop.shared.generated.resources.account_city_detail_not_found
import vitranshop.shared.generated.resources.account_city_detail_subtitle
import vitranshop.shared.generated.resources.account_city_detail_title

@Composable
fun AccountCityDetailScreen(
    cityId: String,
    onBack: () -> Unit,
    onDestClick: (AccountDest) -> Unit,
    onOpenSaved: () -> Unit,
    onFooterLinkClick: (SiteFooterLinkId) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val initial = remember(cityId) { findMockAccountCity(cityId) }
    AccountPageShell(
        dest = AccountDest.Cities,
        onDestClick = onDestClick,
        onSavedClick = onOpenSaved,
        onBack = onBack,
        backTitle = stringResource(Res.string.account_city_detail_title),
        contentMaxWidth = AccountTokens.UsersContentMaxWidth,
        onFooterLinkClick = onFooterLinkClick,
        modifier = modifier,
    ) {
        if (initial == null) {
            AccountCityDetailMissing(onBack = onBack)
        } else {
            AccountCityDetailContent(
                initial = initial,
                onBack = onBack,
            )
        }
    }
}

@Composable
private fun AccountCityDetailContent(
    initial: AccountCity,
    onBack: () -> Unit,
) {
    var saved by remember(initial.id) { mutableStateOf(initial) }
    var draft by remember(initial.id) { mutableStateOf(initial) }
    AccountCityDetailHeader(
        title = stringResource(Res.string.account_city_detail_title),
        subtitle = stringResource(Res.string.account_city_detail_subtitle),
        breadcrumbCurrent = stringResource(Res.string.account_city_detail_title),
        onCitiesClick = onBack,
    )
    AccountCityForm(
        city = draft,
        onCityChange = { draft = it },
        mode = AccountCityFormMode.Edit,
        onPrimary = {
            MockAccountCities.update(draft)
            saved = draft
        },
        onSecondary = { draft = saved },
        onDelete = {
            MockAccountCities.delete(draft.id)
            onBack()
        },
    )
}

@Composable
private fun AccountCityDetailMissing(onBack: () -> Unit) {
    AccountCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(VitranSpacing.xxxl),
            verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
        ) {
            VitranText(
                text = stringResource(Res.string.account_city_detail_not_found),
                style = VitranTextStyle.Title,
            )
            AccountPrimaryButton(
                label = stringResource(Res.string.account_city_detail_back_list),
                onClick = onBack,
            )
        }
    }
}
