package com.vitran.shop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitran.shop.di.vitranKoinViewModel
import com.vitran.shop.feature.admin.moderation.presentation.AdminShopDetailUiState
import com.vitran.shop.feature.admin.moderation.presentation.AdminShopDetailViewModel
import com.vitran.shop.feature.location.presentation.CreateStoreLocationUiState
import com.vitran.shop.feature.location.presentation.CreateStoreLocationViewModel
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.taxonomy.presentation.TaxonomyPickerUiState
import com.vitran.shop.feature.taxonomy.presentation.TaxonomyPickerViewModel
import com.vitran.shop.ui.components.admin.AdminPrimaryButton
import com.vitran.shop.ui.components.admin.AdminSecondaryButton
import com.vitran.shop.ui.components.admin.AdminTokens
import com.vitran.shop.ui.sections.account.stores.AccountStoresError
import com.vitran.shop.ui.sections.admin.AutosaveStatus
import com.vitran.shop.ui.sections.admin.CreateStoreHeaderBar
import com.vitran.shop.ui.sections.admin.CreateStoreStep
import com.vitran.shop.ui.sections.admin.CreateStoreWizardFrame
import com.vitran.shop.ui.sections.admin.toCreateStoreFormState
import com.vitran.shop.ui.sections.reference.toAdminSelectOptions
import com.vitran.shop.ui.sections.reference.toAdminTaxonomyNodes
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.admin_shop_detail_back
import vitranshop.shared.generated.resources.admin_shop_detail_confirm
import vitranshop.shared.generated.resources.admin_shop_detail_confirming
import vitranshop.shared.generated.resources.admin_shop_detail_missing
import vitranshop.shared.generated.resources.admin_shop_detail_subtitle
import vitranshop.shared.generated.resources.admin_shop_detail_title

/**
 * Admin shop review. Route `/admin/moderation/shops/{id}`.
 * Reads the list/confirm cache — there is no admin GET by id — and shows the create-store wizard read-only.
 */
@Composable
fun AdminShopModerationDetailScreen(
    shopId: Long,
    onBack: () -> Unit,
    onViewStore: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: AdminShopDetailViewModel = vitranKoinViewModel { parametersOf(ShopId(shopId)) },
    locationViewModel: CreateStoreLocationViewModel = vitranKoinViewModel(),
    taxonomyViewModel: TaxonomyPickerViewModel = vitranKoinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val locationState by locationViewModel.uiState.collectAsStateWithLifecycle()
    val taxonomyState by taxonomyViewModel.uiState.collectAsStateWithLifecycle()
    val title = stringResource(Res.string.admin_shop_detail_title)
    val subtitle = stringResource(Res.string.admin_shop_detail_subtitle)
    val content = state as? AdminShopDetailUiState.Content
    var step by remember { mutableStateOf(CreateStoreStep.Basics) }
    val cityOptions = when (val current = locationState) {
        is CreateStoreLocationUiState.Content -> current.cities.toAdminSelectOptions()
        else -> emptyList()
    }
    val taxonomyRoots = when (val current = taxonomyState) {
        is TaxonomyPickerUiState.Content -> current.roots.toAdminTaxonomyNodes()
        else -> emptyList()
    }
    if (content == null) {
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
            Column(
                modifier = Modifier.padding(VitranSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
            ) {
                AccountStoresError(
                    message = stringResource(Res.string.admin_shop_detail_missing),
                    onRetry = onBack,
                )
                AdminSecondaryButton(
                    label = stringResource(Res.string.admin_shop_detail_back),
                    onClick = onBack,
                )
            }
        }
        return
    }
    val shop = content.shop
    CreateStoreWizardFrame(
        state = shop.toCreateStoreFormState(),
        step = step,
        onStepClick = { step = it },
        onStateChange = {},
        onBack = onBack,
        modifier = modifier,
        readOnly = true,
        showBottomBar = false,
        headerTitle = title,
        headerSubtitle = subtitle,
        headerTrailing = {
            if (!shop.confirmed) {
                AdminPrimaryButton(
                    label = stringResource(
                        if (content.confirming) {
                            Res.string.admin_shop_detail_confirming
                        } else {
                            Res.string.admin_shop_detail_confirm
                        },
                    ),
                    onClick = viewModel::confirm,
                    enabled = !content.confirming,
                )
            }
        },
        cityOptions = cityOptions,
        citiesLoading = locationState is CreateStoreLocationUiState.Loading,
        citiesError = (locationState as? CreateStoreLocationUiState.Error)?.message,
        onCitiesRetry = locationViewModel::retry,
        onViewStore = onViewStore,
        generalError = content.error?.message,
        taxonomyRoots = taxonomyRoots,
        taxonomyLoading = taxonomyState is TaxonomyPickerUiState.Loading,
        taxonomyError = (taxonomyState as? TaxonomyPickerUiState.Error)?.message,
        onTaxonomyRetry = taxonomyViewModel::retry,
    )
}
