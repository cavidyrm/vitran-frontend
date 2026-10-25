package com.vitran.shop.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitran.shop.di.vitranKoinViewModel
import com.vitran.shop.feature.admin.moderation.presentation.AdminProductDetailsUiState
import com.vitran.shop.feature.admin.moderation.presentation.AdminProductDetailsViewModel
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.taxonomy.presentation.TaxonomyPickerUiState
import com.vitran.shop.feature.taxonomy.presentation.TaxonomyPickerViewModel
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
import com.vitran.shop.ui.components.admin.AdminPrimaryButton
import com.vitran.shop.ui.components.admin.AdminSecondaryButton
import com.vitran.shop.ui.sections.admin.CreateProductFormState
import com.vitran.shop.ui.sections.admin.ProductAdminFormScaffold
import com.vitran.shop.ui.sections.admin.toProductDetailFormState
import com.vitran.shop.ui.sections.reference.toAdminTaxonomyNodes
import com.vitran.shop.ui.theme.VitranSpacing
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.admin_product_detail_breadcrumb
import vitranshop.shared.generated.resources.admin_product_detail_confirm
import vitranshop.shared.generated.resources.admin_product_detail_confirming
import vitranshop.shared.generated.resources.admin_product_detail_error
import vitranshop.shared.generated.resources.admin_product_detail_retry
import vitranshop.shared.generated.resources.admin_product_detail_subtitle
import vitranshop.shared.generated.resources.admin_product_detail_title

/**
 * Platform admin product review. Route `/admin/moderation/products/{id}`.
 * Same two-column form as seller create, read-only, with confirm kept.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AdminProductModerationDetailScreen(
    productId: Long,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AdminProductDetailsViewModel = vitranKoinViewModel {
        parametersOf(ProductId(productId))
    },
    taxonomyViewModel: TaxonomyPickerViewModel = vitranKoinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val taxonomyState by taxonomyViewModel.uiState.collectAsStateWithLifecycle()
    val title = stringResource(Res.string.admin_product_detail_title)
    val subtitle = stringResource(Res.string.admin_product_detail_subtitle)
    val breadcrumb = stringResource(Res.string.admin_product_detail_breadcrumb)
    val titleAnchor = remember { BringIntoViewRequester() }
    val priceAnchor = remember { BringIntoViewRequester() }
    val categoryAnchor = remember { BringIntoViewRequester() }
    val taxonomyRoots = when (val current = taxonomyState) {
        is TaxonomyPickerUiState.Content -> current.roots.toAdminTaxonomyNodes()
        else -> emptyList()
    }
    val content = state as? AdminProductDetailsUiState.Content
    val storeName = content?.product?.shopId?.value?.toString().orEmpty()
    val form = content?.product?.toProductDetailFormState(
        storeName = storeName,
    ) ?: CreateProductFormState(storeName = storeName)
    ProductAdminFormScaffold(
        state = form,
        onBack = onBack,
        titleAnchor = titleAnchor,
        priceAnchor = priceAnchor,
        categoryAnchor = categoryAnchor,
        modifier = modifier,
        readOnly = true,
        storeName = storeName,
        headerTitle = title,
        headerSubtitle = subtitle,
        headerBreadcrumb = breadcrumb,
        headerTrailing = if (content != null && !content.product.confirmed) {
            {
                AdminPrimaryButton(
                    label = stringResource(
                        if (content.confirming) {
                            Res.string.admin_product_detail_confirming
                        } else {
                            Res.string.admin_product_detail_confirm
                        },
                    ),
                    onClick = viewModel::confirm,
                    enabled = !content.confirming,
                )
            }
        } else {
            null
        },
        taxonomyRoots = taxonomyRoots,
        taxonomyLoading = taxonomyState is TaxonomyPickerUiState.Loading,
        taxonomyError = (taxonomyState as? TaxonomyPickerUiState.Error)?.message,
        onTaxonomyRetry = taxonomyViewModel::retry,
        replacement = when (val current = state) {
            AdminProductDetailsUiState.Loading -> {
                { CircularProgressIndicator() }
            }
            is AdminProductDetailsUiState.Error -> {
                val message = current.error.message
                {
                    Column(verticalArrangement = Arrangement.spacedBy(VitranSpacing.md)) {
                        VitranText(
                            text = message ?: stringResource(Res.string.admin_product_detail_error),
                            style = VitranTextStyle.Body,
                        )
                        AdminSecondaryButton(
                            label = stringResource(Res.string.admin_product_detail_retry),
                            onClick = viewModel::load,
                        )
                    }
                }
            }
            is AdminProductDetailsUiState.Content -> null
        },
    )
}
