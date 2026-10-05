package com.vitran.shop.ui.sections.account.stores

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitran.shop.di.vitranKoinViewModel
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.product.domain.model.ProductPublicationState
import com.vitran.shop.feature.seller.product.domain.model.SellerProductSummary
import com.vitran.shop.feature.seller.product.domain.query.SellerProductActiveFilter
import com.vitran.shop.feature.seller.product.presentation.SellerShopProductsViewModel
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.components.admin.AdminSelectField
import com.vitran.shop.ui.components.admin.AdminSelectOption
import com.vitran.shop.ui.components.admin.AdminTaxonomyNode
import com.vitran.shop.ui.components.admin.AdminTaxonomyPicker
import com.vitran.shop.ui.sections.account.AccountCard
import com.vitran.shop.ui.sections.account.AccountOutlinedButton
import com.vitran.shop.ui.sections.account.AccountPrimaryButton
import com.vitran.shop.ui.sections.account.AccountTextLink
import com.vitran.shop.ui.sections.account.AccountTokens
import com.vitran.shop.ui.shell.LocalDesktopLayout
import com.vitran.shop.ui.theme.VitranSize
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.account_store_products_clear_filters
import vitranshop.shared.generated.resources.account_store_products_create
import vitranshop.shared.generated.resources.account_store_products_empty
import vitranshop.shared.generated.resources.account_store_products_error
import vitranshop.shared.generated.resources.account_store_products_filter_active
import vitranshop.shared.generated.resources.account_store_products_filter_all
import vitranshop.shared.generated.resources.account_store_products_filter_category
import vitranshop.shared.generated.resources.account_store_products_filter_clear_category
import vitranshop.shared.generated.resources.account_store_products_filter_inactive
import vitranshop.shared.generated.resources.account_store_products_filter_status
import vitranshop.shared.generated.resources.account_store_products_load_more
import vitranshop.shared.generated.resources.account_store_products_loading
import vitranshop.shared.generated.resources.account_store_products_taxonomy_empty
import vitranshop.shared.generated.resources.account_store_products_taxonomy_loading
import vitranshop.shared.generated.resources.account_store_products_title
import vitranshop.shared.generated.resources.account_stores_loading_more
import vitranshop.shared.generated.resources.account_stores_retry
import vitranshop.shared.generated.resources.account_stores_status_hidden
import vitranshop.shared.generated.resources.account_stores_status_live
import vitranshop.shared.generated.resources.account_stores_status_pending
import vitranshop.shared.generated.resources.account_stores_status_unknown

@Composable
internal fun AccountStoreProductsSection(
    shopId: ShopId,
    taxonomyRoots: List<AdminTaxonomyNode>,
    taxonomyLoading: Boolean,
    taxonomyError: String?,
    onTaxonomyRetry: () -> Unit,
    onCreateProduct: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SellerShopProductsViewModel = vitranKoinViewModel { parametersOf(shopId) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val list = uiState.list
    var skipInitialResume by remember { mutableStateOf(true) }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        if (skipInitialResume) {
            skipInitialResume = false
        } else {
            viewModel.refresh()
        }
    }
    val filtersActive =
        uiState.activeFilter != SellerProductActiveFilter.All || uiState.categorySlug != null
    val isDesktop = LocalDesktopLayout.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.lg),
    ) {
        if (isDesktop) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                VitranText(
                    text = stringResource(Res.string.account_store_products_title),
                    style = VitranTextStyle.Title,
                )
                AccountPrimaryButton(
                    label = stringResource(Res.string.account_store_products_create),
                    onClick = onCreateProduct,
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.md)) {
                VitranText(
                    text = stringResource(Res.string.account_store_products_title),
                    style = VitranTextStyle.Title,
                )
                AccountPrimaryButton(
                    label = stringResource(Res.string.account_store_products_create),
                    onClick = onCreateProduct,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        AccountCard {
            ProductFilters(
                activeFilter = uiState.activeFilter,
                categorySlug = uiState.categorySlug?.value,
                taxonomyRoots = taxonomyRoots,
                taxonomyLoading = taxonomyLoading,
                taxonomyError = taxonomyError,
                showClear = filtersActive,
                onActiveFilter = viewModel::setActiveFilter,
                onCategorySlug = viewModel::setCategorySlug,
                onTaxonomyRetry = onTaxonomyRetry,
                onClear = viewModel::clearFilters,
            )
        }

        when {
            list.items.isEmpty() && list.initialError != null ->
                AccountStoresError(
                    message = list.initialError?.message
                        ?: stringResource(Res.string.account_store_products_error),
                    onRetry = viewModel::refresh,
                )
            list.items.isEmpty() && (list.isLoadingInitial || list.isRefreshing) ->
                AccountStoresLoading(
                    message = stringResource(Res.string.account_store_products_loading),
                )
            list.items.isEmpty() ->
                VitranText(
                    text = stringResource(Res.string.account_store_products_empty),
                    style = VitranTextStyle.Body,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            else -> {
                AccountCard {
                    list.items.forEachIndexed { index, product ->
                        ProductRow(product)
                        if (index < list.items.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = VitranSpacing.lg),
                                thickness = VitranSize.borderHairline,
                                color = AccountTokens.FieldDivider,
                            )
                        }
                    }
                }
                if (list.isLoadingMore) {
                    VitranText(
                        text = stringResource(Res.string.account_stores_loading_more),
                        style = VitranTextStyle.Body,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (list.hasMore && !list.isLoadingMore) {
                    AccountTextLink(
                        label = stringResource(Res.string.account_store_products_load_more),
                        onClick = viewModel::loadNextPage,
                    )
                }
                list.paginationError?.let { error ->
                    AccountStoresError(
                        message = error.message
                            ?: stringResource(Res.string.account_stores_retry),
                        onRetry = viewModel::loadNextPage,
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductFilters(
    activeFilter: SellerProductActiveFilter,
    categorySlug: String?,
    taxonomyRoots: List<AdminTaxonomyNode>,
    taxonomyLoading: Boolean,
    taxonomyError: String?,
    showClear: Boolean,
    onActiveFilter: (SellerProductActiveFilter) -> Unit,
    onCategorySlug: (CategorySlug?) -> Unit,
    onTaxonomyRetry: () -> Unit,
    onClear: () -> Unit,
) {
    val allLabel = stringResource(Res.string.account_store_products_filter_all)
    val activeLabel = stringResource(Res.string.account_store_products_filter_active)
    val inactiveLabel = stringResource(Res.string.account_store_products_filter_inactive)
    val options = listOf(
        AdminSelectOption(id = ActiveAllId, label = allLabel),
        AdminSelectOption(id = ActiveTrueId, label = activeLabel),
        AdminSelectOption(id = ActiveFalseId, label = inactiveLabel),
    )
    val selectedId = when (activeFilter) {
        SellerProductActiveFilter.All -> ActiveAllId
        SellerProductActiveFilter.Active -> ActiveTrueId
        SellerProductActiveFilter.Inactive -> ActiveFalseId
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(VitranSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
    ) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val wide = maxWidth >= VitranSize.mdBreakpoint
        if (wide) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
            ) {
                StatusFilter(
                    selectedId = selectedId,
                    options = options,
                    onActiveFilter = onActiveFilter,
                    modifier = Modifier.weight(1f),
                )
                CategoryFilter(
                    categorySlug = categorySlug,
                    taxonomyRoots = taxonomyRoots,
                    taxonomyLoading = taxonomyLoading,
                    taxonomyError = taxonomyError,
                    onCategorySlug = onCategorySlug,
                    onTaxonomyRetry = onTaxonomyRetry,
                    modifier = Modifier.weight(1f),
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.md)) {
                StatusFilter(
                    selectedId = selectedId,
                    options = options,
                    onActiveFilter = onActiveFilter,
                    modifier = Modifier.fillMaxWidth(),
                )
                CategoryFilter(
                    categorySlug = categorySlug,
                    taxonomyRoots = taxonomyRoots,
                    taxonomyLoading = taxonomyLoading,
                    taxonomyError = taxonomyError,
                    onCategorySlug = onCategorySlug,
                    onTaxonomyRetry = onTaxonomyRetry,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
    if (showClear) {
        AccountOutlinedButton(
            label = stringResource(Res.string.account_store_products_clear_filters),
            onClick = onClear,
        )
    }
    }
}

@Composable
private fun StatusFilter(
    selectedId: String,
    options: List<AdminSelectOption>,
    onActiveFilter: (SellerProductActiveFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    AdminSelectField(
        label = stringResource(Res.string.account_store_products_filter_status),
        valueId = selectedId,
        options = options,
        onSelect = { option ->
            onActiveFilter(
                when (option.id) {
                    ActiveTrueId -> SellerProductActiveFilter.Active
                    ActiveFalseId -> SellerProductActiveFilter.Inactive
                    else -> SellerProductActiveFilter.All
                },
            )
        },
        modifier = modifier,
    )
}

@Composable
private fun CategoryFilter(
    categorySlug: String?,
    taxonomyRoots: List<AdminTaxonomyNode>,
    taxonomyLoading: Boolean,
    taxonomyError: String?,
    onCategorySlug: (CategorySlug?) -> Unit,
    onTaxonomyRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.sm),
    ) {
        AdminTaxonomyPicker(
            label = stringResource(Res.string.account_store_products_filter_category),
            valueId = categorySlug,
            roots = taxonomyRoots,
            onSelect = { node -> onCategorySlug(CategorySlug(node.id)) },
            placeholder = stringResource(Res.string.account_store_products_filter_all),
        )
        if (categorySlug != null) {
            AccountTextLink(
                label = stringResource(Res.string.account_store_products_filter_clear_category),
                onClick = { onCategorySlug(null) },
            )
        }
        when {
            taxonomyLoading ->
                VitranText(
                    text = stringResource(Res.string.account_store_products_taxonomy_loading),
                    style = VitranTextStyle.Label,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            taxonomyError != null ->
                AccountStoresError(message = taxonomyError, onRetry = onTaxonomyRetry)
            taxonomyRoots.isEmpty() ->
                VitranText(
                    text = stringResource(Res.string.account_store_products_taxonomy_empty),
                    style = VitranTextStyle.Label,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
        }
    }
}

@Composable
private fun ProductRow(product: SellerProductSummary) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = VitranSpacing.lg, vertical = VitranSpacing.md),
        verticalArrangement = Arrangement.spacedBy(VitranSpacing.xs),
    ) {
        VitranText(
            text = product.title,
            style = VitranTextStyle.Title,
            maxLines = 1,
        )
        VitranText(
            text = productPublicationLabel(product.publicationState),
            style = VitranTextStyle.Body,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
        )
    }
}

@Composable
private fun productPublicationLabel(state: ProductPublicationState): String =
    stringResource(
        when (state) {
            ProductPublicationState.PendingApproval -> Res.string.account_stores_status_pending
            ProductPublicationState.Live -> Res.string.account_stores_status_live
            ProductPublicationState.ApprovedHidden -> Res.string.account_stores_status_hidden
            ProductPublicationState.Inconsistent -> Res.string.account_stores_status_unknown
        },
    )

private const val ActiveAllId = "all"
private const val ActiveTrueId = "true"
private const val ActiveFalseId = "false"
