package com.vitran.shop.ui.sections.categories

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitran.shop.feature.marketplace.product.presentation.ProductListUiState
import com.vitran.shop.feature.marketplace.di.ProductListViewModelFactory
import com.vitran.shop.feature.marketplace.di.ShopBrowseViewModelFactory
import com.vitran.shop.feature.marketplace.shop.presentation.ShopBrowseUiState
import com.vitran.shop.ui.sections.reference.toCategoriesMerchantShop
import com.vitran.shop.ui.sections.reference.toCategoriesProduct
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import vitranshop.shared.generated.resources.Res
import vitranshop.shared.generated.resources.categories_product_row_top_rated_home

@Composable
fun CategoriesMarketplaceFeed(
    modifier: Modifier = Modifier,
    onProductOpen: (
        id: String,
        title: String,
        imageUrl: String,
        storeName: String,
        priceLabel: String,
    ) -> Unit,
    onStoreOpen: (shopId: String) -> Unit,
    fallbackProductRows: List<CategoriesProductRowSection>,
    fallbackMerchantGrids: List<CategoriesMerchantGridSection>,
) {
    val shopFactory: ShopBrowseViewModelFactory = koinInject()
    val productFactory: ProductListViewModelFactory = koinInject()
    val shopViewModel = remember { shopFactory.create(categorySlug = null) }
    val productViewModel = remember { productFactory.create(categorySlug = null) }
    val shopState by shopViewModel.uiState.collectAsStateWithLifecycle()
    val productState by productViewModel.uiState.collectAsStateWithLifecycle()

    val productRows = when (val state = productState) {
        is ProductListUiState.Content -> listOf(
            CategoriesProductRowSection(
                id = "api-products",
                title = stringResource(Res.string.categories_product_row_top_rated_home),
                products = state.products.items.map { it.toCategoriesProduct() },
            ),
        )
        else -> fallbackProductRows
    }

    val merchantGrids = when (val state = shopState) {
        is ShopBrowseUiState.Content -> listOf(
            CategoriesMerchantGridSection(
                id = "api-shops",
                title = stringResource(Res.string.categories_product_row_top_rated_home),
                shops = state.shops.items.map { it.toCategoriesMerchantShop() },
            ),
        )
        else -> fallbackMerchantGrids
    }

    CategoriesProductRowsFeed(
        sections = productRows,
        modifier = modifier.fillMaxWidth(),
        onSectionClick = { /* category landing deferred */ },
        onProductClick = { _, product ->
            onProductOpen(
                product.id,
                product.title,
                product.imageUrl,
                product.storeName,
                product.priceLabel,
            )
        },
        onSaveClick = { _, _ -> /* Phase 6 — favorites */ },
    )
    CategoriesMerchantGridsFeed(
        sections = merchantGrids,
        modifier = modifier.fillMaxWidth(),
        onSectionClick = { /* category landing deferred */ },
        onShopClick = { _, shop -> onStoreOpen(shop.id) },
    )
}
