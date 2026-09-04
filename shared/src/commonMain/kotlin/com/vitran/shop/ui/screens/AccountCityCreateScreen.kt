package com.vitran.shop.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitran.shop.feature.admin.catalog.location.presentation.AdminCityCreateViewModel
import com.vitran.shop.ui.components.SiteFooterLinkId
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.sections.account.AccountDest
import com.vitran.shop.ui.sections.account.AccountPageShell
import com.vitran.shop.ui.sections.account.AccountTokens
import com.vitran.shop.ui.sections.account.cities.AccountCity
import com.vitran.shop.ui.sections.account.cities.AccountCityDetailHeader
import com.vitran.shop.ui.sections.account.cities.AccountCityForm
import com.vitran.shop.ui.sections.account.cities.AccountCityFormMode
import com.vitran.shop.di.vitranKoinViewModel
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
    viewModel: AdminCityCreateViewModel = vitranKoinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var draft by remember { mutableStateOf(AccountCity(id = 0, name = "", slug = "")) }
    LaunchedEffect(state.createdCity) {
        if (state.createdCity != null) onBack()
    }
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
                    viewModel.create(slug = draft.slug, name = draft.name)
                }
            },
            onSecondary = onBack,
            nameError = state.fieldErrors["name"],
            slugError = state.fieldErrors["slug"],
            onClearFieldError = viewModel::clearFieldError,
        )
        state.error?.let {
            VitranText(it.message ?: "ثبت شهر انجام نشد", VitranTextStyle.Body)
        }
    }
}
