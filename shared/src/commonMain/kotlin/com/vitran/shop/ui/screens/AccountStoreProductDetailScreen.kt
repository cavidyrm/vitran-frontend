package com.vitran.shop.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitran.shop.di.vitranKoinViewModel
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.seller.product.presentation.SellerProductDetailUiState
import com.vitran.shop.feature.seller.product.presentation.SellerProductDetailViewModel
import com.vitran.shop.feature.taxonomy.presentation.TaxonomyPickerUiState
import com.vitran.shop.feature.taxonomy.presentation.TaxonomyPickerViewModel
import com.vitran.shop.ui.components.VitranText
import com.vitran.shop.ui.components.VitranTextStyle
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
import vitranshop.shared.generated.resources.admin_product_detail_error
import vitranshop.shared.generated.resources.admin_product_detail_retry
import vitranshop.shared.generated.resources.admin_product_detail_subtitle
import vitranshop.shared.generated.resources.admin_product_detail_title

/**
 * Seller product detail. Route `/account/stores/{shopId}/products/{productId}`.
 * Same read-only form as admin review and seller create. No save bar.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AccountStoreProductDetailScreen(
    shopId: String,
    productId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val parsedId = productId.toLongOrNull()?.takeIf { it > 0L }
    if (parsedId == null) {
        ProductDetailFormMessage(
            shopId = shopId,
            onBack = onBack,
            modifier = modifier,
            message = stringResource(Res.string.admin_product_detail_error),
        )
    } else {
        AccountStoreProductDetailBody(
            shopId = shopId,
            productId = ProductId(parsedId),
            onBack = onBack,
            modifier = modifier,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AccountStoreProductDetailBody(
    shopId: String,
    productId: ProductId,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SellerProductDetailViewModel = vitranKoinViewModel { parametersOf(productId) },
    taxonomyViewModel: TaxonomyPickerViewModel = vitranKoinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val taxonomyState by taxonomyViewModel.uiState.collectAsStateWithLifecycle()
    val titleAnchor = remember { BringIntoViewRequester() }
    val priceAnchor = remember { BringIntoViewRequester() }
    val categoryAnchor = remember { BringIntoViewRequester() }
    val taxonomyRoots = when (val current = taxonomyState) {
        is TaxonomyPickerUiState.Content -> current.roots.toAdminTaxonomyNodes()
        else -> emptyList()
    }
    val content = state as? SellerProductDetailUiState.Content
    val storeName = content?.product?.shopId?.value?.toString() ?: shopId
    val form = content?.product?.toProductDetailFormState(storeName = storeName)
        ?: CreateProductFormState(storeName = storeName)
    ProductAdminFormScaffold(
        state = form,
        onBack = onBack,
        titleAnchor = titleAnchor,
        priceAnchor = priceAnchor,
        categoryAnchor = categoryAnchor,
        modifier = modifier,
        readOnly = true,
        storeName = storeName,
        headerTitle = stringResource(Res.string.admin_product_detail_title),
        headerSubtitle = stringResource(Res.string.admin_product_detail_subtitle),
        headerBreadcrumb = stringResource(Res.string.admin_product_detail_breadcrumb),
        taxonomyRoots = taxonomyRoots,
        taxonomyLoading = taxonomyState is TaxonomyPickerUiState.Loading,
        taxonomyError = (taxonomyState as? TaxonomyPickerUiState.Error)?.message,
        onTaxonomyRetry = taxonomyViewModel::retry,
        replacement = when (val current = state) {
            SellerProductDetailUiState.Loading -> {
                { CircularProgressIndicator() }
            }
            is SellerProductDetailUiState.Error -> {
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
            is SellerProductDetailUiState.Content -> null
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ProductDetailFormMessage(
    shopId: String,
    onBack: () -> Unit,
    message: String,
    modifier: Modifier = Modifier,
) {
    val anchor = remember { BringIntoViewRequester() }
    ProductAdminFormScaffold(
        state = CreateProductFormState(storeName = shopId),
        onBack = onBack,
        titleAnchor = anchor,
        priceAnchor = anchor,
        categoryAnchor = anchor,
        modifier = modifier,
        readOnly = true,
        storeName = shopId,
        headerTitle = stringResource(Res.string.admin_product_detail_title),
        headerSubtitle = stringResource(Res.string.admin_product_detail_subtitle),
        headerBreadcrumb = stringResource(Res.string.admin_product_detail_breadcrumb),
        replacement = {
            VitranText(text = message, style = VitranTextStyle.Body)
        },
    )
}
