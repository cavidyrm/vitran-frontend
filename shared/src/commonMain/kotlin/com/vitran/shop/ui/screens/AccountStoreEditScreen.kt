package com.vitran.shop.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitran.shop.di.vitranKoinViewModel
import com.vitran.shop.feature.location.domain.model.CityId
import com.vitran.shop.feature.location.presentation.CreateStoreLocationUiState
import com.vitran.shop.feature.location.presentation.CreateStoreLocationViewModel
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.shop.presentation.EditShopViewModel
import com.vitran.shop.feature.taxonomy.presentation.TaxonomyPickerUiState
import com.vitran.shop.feature.taxonomy.presentation.TaxonomyPickerViewModel
import com.vitran.shop.ui.components.SiteFooterLinkId
import com.vitran.shop.ui.sections.account.AccountDest
import com.vitran.shop.ui.sections.account.AccountPageShell
import com.vitran.shop.ui.sections.account.stores.AccountStoreEditForm
import com.vitran.shop.ui.sections.account.stores.AccountStoreProductsSection
import com.vitran.shop.ui.sections.account.stores.AccountStoresError
import com.vitran.shop.ui.sections.account.stores.AccountStoresLoading
import com.vitran.shop.ui.theme.VitranSpacing
import com.vitran.shop.ui.sections.reference.toAdminSelectOptions
import com.vitran.shop.ui.sections.reference.toAdminTaxonomyNodes
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_store_edit_error
import vitranshop.shared.generated.resources.account_store_edit_invalid
import vitranshop.shared.generated.resources.account_store_edit_loading
import vitranshop.shared.generated.resources.account_store_edit_saved
import vitranshop.shared.generated.resources.account_store_edit_title

/**
 * Edit an owned shop — route `/account/stores/{shopId}`.
 * Loads `GET /seller/shops/{id}` and saves with `PATCH /seller/shops/{id}`.
 */
@Composable
fun AccountStoreEditScreen(
    shopId: String,
    onBack: () -> Unit,
    onDestClick: (AccountDest) -> Unit,
    onOpenSaved: () -> Unit,
    onCreateProduct: () -> Unit = {},
    onFooterLinkClick: (SiteFooterLinkId) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val title = stringResource(Res.string.account_store_edit_title)
    val parsedId = shopId.toLongOrNull()
    AccountPageShell(
        dest = AccountDest.Stores,
        onDestClick = onDestClick,
        onSavedClick = onOpenSaved,
        onBack = onBack,
        backTitle = title,
        onFooterLinkClick = onFooterLinkClick,
        modifier = modifier,
    ) {
        if (parsedId == null || parsedId <= 0L) {
            AccountStoresError(
                message = stringResource(Res.string.account_store_edit_invalid),
                onRetry = onBack,
            )
        } else {
            AccountStoreEditBody(
                shopId = ShopId(parsedId),
                onCreateProduct = onCreateProduct,
            )
        }
    }
}

@Composable
private fun AccountStoreEditBody(
    shopId: ShopId,
    onCreateProduct: () -> Unit,
    viewModel: EditShopViewModel = vitranKoinViewModel { parametersOf(shopId) },
    locationViewModel: CreateStoreLocationViewModel = vitranKoinViewModel(),
    taxonomyViewModel: TaxonomyPickerViewModel = vitranKoinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val locationState by locationViewModel.uiState.collectAsStateWithLifecycle()
    val taxonomyState by taxonomyViewModel.uiState.collectAsStateWithLifecycle()
    val cityOptions = when (val current = locationState) {
        is CreateStoreLocationUiState.Content -> current.cities.toAdminSelectOptions()
        else -> emptyList()
    }
    val taxonomyRoots = when (val current = taxonomyState) {
        is TaxonomyPickerUiState.Content -> current.roots.toAdminTaxonomyNodes()
        else -> emptyList()
    }
    when {
        uiState.isLoading ->
            AccountStoresLoading(
                message = stringResource(Res.string.account_store_edit_loading),
            )
        uiState.loadError != null && uiState.loadedShop == null ->
            AccountStoresError(
                message = uiState.loadError?.message
                    ?: stringResource(Res.string.account_store_edit_error),
                onRetry = viewModel::load,
            )
        else ->
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
            ) {
            AccountStoreEditForm(
                form = uiState.form,
                publication = uiState.loadedShop?.publicationState,
                fieldErrors = uiState.fieldErrors,
                slugCheck = uiState.slugCheck,
                generalError = uiState.generalError?.message,
                savedMessage = if (uiState.savedShop != null) {
                    stringResource(Res.string.account_store_edit_saved)
                } else {
                    null
                },
                isSubmitting = uiState.isSubmitting,
                cityOptions = cityOptions,
                citiesLoading = locationState is CreateStoreLocationUiState.Loading,
                citiesError = (locationState as? CreateStoreLocationUiState.Error)?.message,
                onCitiesRetry = locationViewModel::retry,
                taxonomyRoots = taxonomyRoots,
                taxonomyLoading = taxonomyState is TaxonomyPickerUiState.Loading,
                taxonomyError = (taxonomyState as? TaxonomyPickerUiState.Error)?.message,
                onTaxonomyRetry = taxonomyViewModel::retry,
                onTitleChange = { value -> viewModel.updateForm { it.copy(title = value) } },
                onSlugChange = viewModel::onSlugChanged,
                onDescriptionChange = { value -> viewModel.updateForm { it.copy(description = value) } },
                onAddressChange = { value -> viewModel.updateForm { it.copy(address = value) } },
                onPhoneChange = { value -> viewModel.updateForm { it.copy(phoneNumber = value) } },
                onSupportTimesChange = { value -> viewModel.updateForm { it.copy(supportTimes = value) } },
                onAvatarChange = { value -> viewModel.updateForm { it.copy(avatarUrl = value) } },
                onWhatsappChange = { value -> viewModel.updateForm { it.copy(whatsapp = value) } },
                onTelegramChange = { value -> viewModel.updateForm { it.copy(telegram = value) } },
                onInstagramChange = { value -> viewModel.updateForm { it.copy(instagram = value) } },
                onWebsiteChange = { value -> viewModel.updateForm { it.copy(website = value) } },
                onCitySelect = { id ->
                    viewModel.updateForm { form ->
                        form.copy(cityId = id.toLongOrNull()?.let(::CityId))
                    }
                },
                onToggleCategory = viewModel::toggleCategory,
                onSave = viewModel::save,
            )
            AccountStoreProductsSection(
                shopId = shopId,
                taxonomyRoots = taxonomyRoots,
                taxonomyLoading = taxonomyState is TaxonomyPickerUiState.Loading,
                taxonomyError = (taxonomyState as? TaxonomyPickerUiState.Error)?.message,
                onTaxonomyRetry = taxonomyViewModel::retry,
                onCreateProduct = onCreateProduct,
            )
            }
    }
}
