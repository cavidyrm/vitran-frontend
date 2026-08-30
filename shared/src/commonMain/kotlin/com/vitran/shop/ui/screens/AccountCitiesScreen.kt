package com.vitran.shop.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitran.shop.feature.admin.catalog.location.presentation.AdminCitiesUiState
import com.vitran.shop.feature.admin.catalog.location.presentation.AdminCitiesViewModel
import com.vitran.shop.ui.components.SiteFooterLinkId
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.sections.account.AccountDest
import com.vitran.shop.ui.sections.account.AccountPageShell
import com.vitran.shop.ui.sections.account.AccountTokens
import com.vitran.shop.ui.sections.account.cities.AccountCitiesFilters
import com.vitran.shop.ui.sections.account.cities.AccountCitiesHeader
import com.vitran.shop.ui.sections.account.cities.AccountCitiesTable
import com.vitran.shop.ui.sections.account.cities.filterAccountCities
import com.vitran.shop.ui.sections.account.cities.toAccountCity
import com.vitran.shop.di.vitranKoinViewModel
import org.jetbrains.compose.resources.stringResource
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_nav_cities

@Composable
fun AccountCitiesScreen(
    onBack: () -> Unit,
    onDestClick: (AccountDest) -> Unit,
    onOpenSaved: () -> Unit,
    onCityOpen: (Int) -> Unit,
    onAddCity: () -> Unit,
    onFooterLinkClick: (SiteFooterLinkId) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: AdminCitiesViewModel = vitranKoinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var search by remember { mutableStateOf("") }

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
        AccountCitiesHeader(onAddClick = onAddCity)
        AccountCitiesFilters(
            search = search,
            onSearchChange = { search = it },
        )
        when (val state = uiState) {
            is AdminCitiesUiState.Content -> {
                AccountCitiesTable(
                    cities = filterAccountCities(state.cities.map { it.toAccountCity() }, search),
                    onCityClick = { city -> onCityOpen(city.id) },
                    onActiveChange = { _, _ -> },
                )
            }
            AdminCitiesUiState.Empty ->
                VitranText("شهری یافت نشد", VitranTextStyle.Body)
            is AdminCitiesUiState.Error ->
                VitranText(state.error.message ?: "خطا در دریافت شهرها", VitranTextStyle.Body)
            AdminCitiesUiState.Loading ->
                VitranText("در حال دریافت شهرها…", VitranTextStyle.Body)
        }
    }
}
