package com.vitran.shop.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.vitran.shop.ui.components.SiteFooterLinkId
import com.vitran.shop.ui.sections.account.AccountDest
import com.vitran.shop.ui.sections.account.AccountPageShell
import com.vitran.shop.ui.sections.account.AccountTokens
import com.vitran.shop.ui.sections.account.cities.AccountCity
import com.vitran.shop.ui.sections.account.cities.AccountCityDetailHeader
import com.vitran.shop.ui.sections.account.cities.AccountCityForm
import com.vitran.shop.ui.sections.account.cities.AccountCityFormMode
import com.vitran.shop.ui.sections.account.cities.MockAccountCities
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_city_create_subtitle
import vitranshop.shared.generated.resources.account_city_create_title
import vitranshop.shared.generated.resources.account_nav_cities

@Composable
fun AccountCityCreateScreen(
    onBack: () -> Unit,
    onDestClick: (AccountDest) -> Unit,
    onOpenSaved: () -> Unit,
    onFooterLinkClick: (SiteFooterLinkId) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var draft by remember { mutableStateOf(AccountCity(id = 0, name = "", slug = "")) }
    AccountPageShell(
        dest = AccountDest.Cities,
        onDestClick = onDestClick,
        onSavedClick = onOpenSaved,
        onBack = onBack,
        backTitle = stringResource(Res.string.account_nav_cities),
        contentMaxWidth = AccountTokens.UsersContentMaxWidth,
        onFooterLinkClick = onFooterLinkClick,
        modifier = modifier,
    ) {
        AccountCityDetailHeader(
            title = stringResource(Res.string.account_city_create_title),
            subtitle = stringResource(Res.string.account_city_create_subtitle),
            breadcrumbCurrent = stringResource(Res.string.account_city_create_title),
            onCitiesClick = onBack,
            onAddClick = {},
        )
        AccountCityForm(
            city = draft,
            onCityChange = { draft = it },
            mode = AccountCityFormMode.Create,
            onPrimary = {
                if (draft.name.isNotBlank() && draft.slug.isNotBlank()) {
                    MockAccountCities.add(
                        name = draft.name,
                        slug = draft.slug,
                        isActive = draft.isActive,
                    )
                    onBack()
                }
            },
            onSecondary = onBack,
        )
    }
}
