package com.vitran.shop.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.vitran.shop.ui.sections.account.AccountDest
import com.vitran.shop.ui.sections.account.AccountPageShell
import com.vitran.shop.ui.sections.account.AccountTokens
import com.vitran.shop.ui.sections.account.cities.AccountCitiesFilters
import com.vitran.shop.ui.sections.account.cities.AccountCitiesHeader
import com.vitran.shop.ui.sections.account.cities.AccountCitiesTable
import com.vitran.shop.ui.sections.account.cities.MockAccountCities
import com.vitran.shop.ui.sections.account.cities.filterAccountCities
import com.vitran.shop.ui.sections.account.cities.rememberMockAccountCities
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
    modifier: Modifier = Modifier,
) {
    val cities = rememberMockAccountCities()
    var search by remember { mutableStateOf("") }
    val filtered = filterAccountCities(cities, search)

    AccountPageShell(
        dest = AccountDest.Cities,
        onDestClick = onDestClick,
        onSavedClick = onOpenSaved,
        onBack = onBack,
        backTitle = stringResource(Res.string.account_nav_cities),
        contentMaxWidth = AccountTokens.UsersContentMaxWidth,
        modifier = modifier,
    ) {
        AccountCitiesHeader(onAddClick = onAddCity)
        AccountCitiesFilters(
            search = search,
            onSearchChange = { search = it },
        )
        AccountCitiesTable(
            cities = filtered,
            onCityClick = { city -> onCityOpen(city.id) },
            onActiveChange = { city, isActive ->
                MockAccountCities.update(city.copy(isActive = isActive))
            },
        )
    }
}
