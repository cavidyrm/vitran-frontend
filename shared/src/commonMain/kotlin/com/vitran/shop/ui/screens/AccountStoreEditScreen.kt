package com.vitran.shop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitran.shop.di.vitranKoinViewModel
import com.vitran.shop.feature.location.presentation.CreateStoreLocationUiState
import com.vitran.shop.feature.location.presentation.CreateStoreLocationViewModel
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.shop.presentation.EditShopViewModel
import com.vitran.shop.feature.seller.shop.presentation.SlugCheckUiStatus
import com.vitran.shop.feature.taxonomy.presentation.TaxonomyPickerUiState
import com.vitran.shop.feature.taxonomy.presentation.TaxonomyPickerViewModel
import com.vitran.shop.ui.components.SiteFooterLinkId
import com.vitran.shop.ui.components.admin.AdminTokens
import com.vitran.shop.ui.sections.account.AccountDest
import com.vitran.shop.ui.sections.account.stores.AccountStoreCollectionsSection
import com.vitran.shop.ui.sections.account.stores.AccountStoreProductsSection
import com.vitran.shop.ui.sections.account.stores.AccountStoresError
import com.vitran.shop.ui.sections.account.stores.AccountStoresLoading
import com.vitran.shop.ui.sections.admin.AutosaveStatus
import com.vitran.shop.ui.sections.admin.CreateStoreHeaderBar
import com.vitran.shop.ui.sections.admin.CreateStoreStep
import com.vitran.shop.ui.sections.admin.CreateStoreStepOrder
import com.vitran.shop.ui.sections.admin.CreateStoreWizardFrame
import com.vitran.shop.ui.sections.admin.appliedTo
import com.vitran.shop.ui.sections.admin.toCreateStoreFormState
import com.vitran.shop.ui.sections.reference.toAdminSelectOptions
import com.vitran.shop.ui.sections.reference.toAdminTaxonomyNodes
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_store_edit_error
import vitranshop.shared.generated.resources.account_store_edit_invalid
import vitranshop.shared.generated.resources.account_store_edit_loading
import vitranshop.shared.generated.resources.account_store_edit_save
import vitranshop.shared.generated.resources.account_store_edit_saved
import vitranshop.shared.generated.resources.account_store_edit_subtitle
import vitranshop.shared.generated.resources.account_store_edit_title

/**
 * Edit an owned shop — route `/account/stores/{shopId}`.
 * Same create-store wizard as `/admin/stores/new`. Save stays `PATCH /seller/shops/{id}`.
 * Account chrome is hidden. Collections and products stay under the form.
 */
@Composable
@Suppress("UNUSED_PARAMETER")
fun AccountStoreEditScreen(
    shopId: String,
    onBack: () -> Unit,
    onDestClick: (AccountDest) -> Unit,
    onOpenSaved: () -> Unit,
    onCreateProduct: () -> Unit = {},
    onProductOpen: (Long) -> Unit = {},
    onFooterLinkClick: (SiteFooterLinkId) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val title = stringResource(Res.string.account_store_edit_title)
    val subtitle = stringResource(Res.string.account_store_edit_subtitle)
    val parsedId = shopId.toLongOrNull()
    if (parsedId == null || parsedId <= 0L) {
        StoreEditMessage(
            title = title,
            subtitle = subtitle,
            onBack = onBack,
            modifier = modifier,
        ) {
            AccountStoresError(
                message = stringResource(Res.string.account_store_edit_invalid),
                onRetry = onBack,
            )
        }
        return
    }
    AccountStoreEditBody(
        shopId = ShopId(parsedId),
        title = title,
        subtitle = subtitle,
        onBack = onBack,
        onCreateProduct = onCreateProduct,
        onProductOpen = onProductOpen,
        modifier = modifier,
    )
}

@Composable
private fun AccountStoreEditBody(
    shopId: ShopId,
    title: String,
    subtitle: String,
    onBack: () -> Unit,
    onCreateProduct: () -> Unit,
    onProductOpen: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditShopViewModel = vitranKoinViewModel { parametersOf(shopId) },
    locationViewModel: CreateStoreLocationViewModel = vitranKoinViewModel(),
    taxonomyViewModel: TaxonomyPickerViewModel = vitranKoinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val locationState by locationViewModel.uiState.collectAsStateWithLifecycle()
    val taxonomyState by taxonomyViewModel.uiState.collectAsStateWithLifecycle()
    var step by remember { mutableStateOf(CreateStoreStep.Basics) }
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
            StoreEditMessage(title, subtitle, onBack, modifier) {
                AccountStoresLoading(message = stringResource(Res.string.account_store_edit_loading))
            }
        uiState.loadError != null && uiState.loadedShop == null ->
            StoreEditMessage(title, subtitle, onBack, modifier) {
                AccountStoresError(
                    message = uiState.loadError?.message
                        ?: stringResource(Res.string.account_store_edit_error),
                    onRetry = viewModel::load,
                )
            }
        else -> {
            val wizard = uiState.form.toCreateStoreFormState()
            val saveLabel = stringResource(Res.string.account_store_edit_save)
            CreateStoreWizardFrame(
                state = wizard,
                step = step,
                onStepClick = { step = it },
                onStateChange = { next ->
                    val current = viewModel.uiState.value.form
                    val merged = next.appliedTo(current)
                    val slugChanged = merged.slug != current.slug
                    viewModel.updateForm { merged }
                    if (slugChanged) viewModel.onSlugChanged(merged.slug)
                },
                onBack = onBack,
                modifier = modifier,
                headerTitle = title,
                headerSubtitle = subtitle,
                cityOptions = cityOptions,
                citiesLoading = locationState is CreateStoreLocationUiState.Loading,
                citiesError = (locationState as? CreateStoreLocationUiState.Error)?.message,
                onCitiesRetry = locationViewModel::retry,
                fieldErrors = uiState.fieldErrors,
                generalError = uiState.generalError?.message,
                banner = if (uiState.savedShop != null) {
                    stringResource(Res.string.account_store_edit_saved)
                } else {
                    null
                },
                slugCheck = uiState.slugCheck,
                taxonomyRoots = taxonomyRoots,
                taxonomyLoading = taxonomyState is TaxonomyPickerUiState.Loading,
                taxonomyError = (taxonomyState as? TaxonomyPickerUiState.Error)?.message,
                onTaxonomyRetry = taxonomyViewModel::retry,
                canPublish = wizard.canPublish && wizard.cityId?.toLongOrNull() != null,
                published = false,
                onBottomBack = {
                    val index = CreateStoreStepOrder.indexOf(step)
                    if (index > 0) step = CreateStoreStepOrder[index - 1] else onBack()
                },
                onBottomNext = {
                    val index = CreateStoreStepOrder.indexOf(step)
                    if (index < CreateStoreStepOrder.lastIndex) {
                        step = CreateStoreStepOrder[index + 1]
                    }
                },
                onPublish = viewModel::save,
                lastStepLabel = saveLabel,
                lastStepEnabled = !uiState.isSubmitting && uiState.slugCheck !is SlugCheckUiStatus.Taken,
                belowForm = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
                    ) {
                        AccountStoreCollectionsSection(shopId = shopId)
                        AccountStoreProductsSection(
                            shopId = shopId,
                            taxonomyRoots = taxonomyRoots,
                            taxonomyLoading = taxonomyState is TaxonomyPickerUiState.Loading,
                            taxonomyError = (taxonomyState as? TaxonomyPickerUiState.Error)?.message,
                            onTaxonomyRetry = taxonomyViewModel::retry,
                            onCreateProduct = onCreateProduct,
                            onProductOpen = onProductOpen,
                        )
                    }
                },
            )
        }
    }
}

@Composable
private fun StoreEditMessage(
    title: String,
    subtitle: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AdminTokens.PageBackground),
    ) {
        CreateStoreHeaderBar(
            autosaveStatus = AutosaveStatus.Idle,
            onBack = onBack,
            title = title,
            subtitle = subtitle,
        )
        Column(modifier = Modifier.padding(VitranSpacing.lg)) {
            content()
        }
    }
}
