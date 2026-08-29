package com.vitran.shop.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitran.shop.di.rememberProductSearchViewModel
import com.vitran.shop.feature.marketplace.product.presentation.ProductSearchUiState
import com.vitran.shop.ui.components.CategoriesProductCard
import com.vitran.shop.ui.sections.reference.toCategoriesProduct
import com.vitran.shop.ui.theme.VitranSpacing

@Composable
fun SearchResultsScreen(
    query: String,
    modifier: Modifier = Modifier,
    onProductOpen: (
        id: String,
        title: String,
        imageUrl: String,
        storeName: String,
        priceLabel: String,
    ) -> Unit = { _, _, _, _, _ -> },
) {
    val viewModel = rememberProductSearchViewModel(query)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        when (val state = uiState) {
            is ProductSearchUiState.Idle -> {
                SearchMessage("عبارت جستجو را وارد کنید")
            }
            is ProductSearchUiState.Searching -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ProductSearchUiState.Empty -> {
                SearchMessage("نتیجه‌ای برای «${state.query}» یافت نشد")
            }
            is ProductSearchUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(VitranSpacing.lg),
                    verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
                ) {
                    Text(
                        text = state.message ?: "خطا در جستجو",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    TextButton(onClick = viewModel::retry) {
                        Text("تلاش دوباره")
                    }
                }
            }
            is ProductSearchUiState.Results -> {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    contentPadding = PaddingValues(VitranSpacing.lg),
                    horizontalArrangement = Arrangement.spacedBy(VitranSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(VitranSpacing.md),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(state.results.items, key = { it.id.value }) { product ->
                        val uiProduct = product.toCategoriesProduct()
                        CategoriesProductCard(
                            product = uiProduct,
                            showStoreName = true,
                            onClick = {
                                onProductOpen(
                                    uiProduct.id,
                                    uiProduct.title,
                                    uiProduct.imageUrl,
                                    uiProduct.storeName,
                                    uiProduct.priceLabel,
                                )
                            },
                        )
                    }
                }
                if (state.results.isLoadingMore) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(VitranSpacing.lg),
                    )
                }
                if (state.results.paginationError != null) {
                    TextButton(
                        onClick = viewModel::retryPagination,
                        modifier = Modifier.align(Alignment.BottomCenter),
                    ) {
                        Text("بارگذاری بیشتر")
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchMessage(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .fillMaxWidth()
            .padding(VitranSpacing.lg),
    )
}
