package com.vitran.shop.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitran.shop.feature.admin.catalog.location.presentation.AdminCityDetailViewModel
import com.vitran.shop.feature.location.domain.model.CityId
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
import com.vitran.shop.ui.sections.account.cities.toAccountCity
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.di.vitranKoinViewModel
import org.koin.core.parameter.parametersOf
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
    viewModel: AdminCityDetailViewModel = vitranKoinViewModel {
        parametersOf(CityId(cityId.toLongOrNull() ?: 0L))
    },
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(state.deleted) {
        if (state.deleted) onBack()
    }
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
        val city = state.city
        if (state.isLoading) {
            VitranText("در حال دریافت شهر…", VitranTextStyle.Body)
        } else if (city == null) {
            AccountCityDetailMissing(onBack = onBack)
        } else {
            AccountCityDetailContent(
                initial = city.toAccountCity(),
                canDelete = state.canDelete,
                onBack = onBack,
                onSave = { draft -> viewModel.update(draft.slug, draft.name) },
                onDelete = viewModel::delete,
            )
        }
        state.mutationError?.let {
            VitranText(it.message ?: "ویرایش شهر انجام نشد", VitranTextStyle.Body)
        }
        state.deleteError?.let {
            VitranText("حذف شهر انجام نشد", VitranTextStyle.Body)
        }
    }
}

@Composable
private fun AccountCityDetailContent(
    initial: AccountCity,
    canDelete: Boolean,
    onBack: () -> Unit,
    onSave: (AccountCity) -> Unit,
    onDelete: () -> Unit,
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
            onSave(draft)
            saved = draft
        },
        onSecondary = { draft = saved },
        onDelete = onDelete.takeIf { canDelete },
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
