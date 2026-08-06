package com.vitran.shop.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.vitran.shop.ui.screens.AccountScreen
import com.vitran.shop.ui.screens.CategoriesScreen
import com.vitran.shop.ui.screens.HomeScreen
import com.vitran.shop.ui.screens.OffersScreen
import com.vitran.shop.ui.screens.ProductDetailScreen
import com.vitran.shop.ui.screens.SavedScreen
import com.vitran.shop.ui.sections.product.MockProductCatalog

/**
 * Sole place that maps [Route] → screen UI via Navigation 3 [NavDisplay].
 * Does not own chrome — [com.vitran.shop.ui.shell.AppShell] remains the layout owner.
 */
@Composable
fun AppNavHost(
    navState: NavigationState,
    navigator: Navigator,
    modifier: Modifier = Modifier,
) {
    NavDisplay(
        backStack = navState.backStack,
        modifier = modifier,
        onBack = { navigator.goBack() },
        entryProvider = entryProvider {
            entry<Route.Home> {
                HomeScreen(
                    onProductOpen = { id, title, imageUrl, storeName, priceLabel ->
                        val product = MockProductCatalog.resolve(
                            id = id,
                            title = title,
                            imageUrl = imageUrl,
                            storeName = storeName,
                            priceLabel = priceLabel,
                        )
                        navigator.push(
                            Route.ProductDetail(
                                productId = product.id,
                                slug = product.slug,
                            ),
                        )
                    },
                )
            }
            entry<Route.Categories> {
                CategoriesScreen(
                    onProductOpen = { id, title, imageUrl, storeName, priceLabel ->
                        val product = MockProductCatalog.resolve(
                            id = id,
                            title = title,
                            imageUrl = imageUrl,
                            storeName = storeName,
                            priceLabel = priceLabel,
                        )
                        navigator.push(
                            Route.ProductDetail(
                                productId = product.id,
                                slug = product.slug,
                            ),
                        )
                    },
                )
            }
            entry<Route.Offers> { OffersScreen() }
            entry<Route.Saved> { SavedScreen() }
            entry<Route.Account> { AccountScreen() }
            entry<Route.ProductDetail> { key ->
                ProductDetailScreen(
                    productId = key.productId,
                    onProductOpen = { id, title, imageUrl, storeName, priceLabel ->
                        val product = MockProductCatalog.resolve(
                            id = id,
                            title = title,
                            imageUrl = imageUrl,
                            storeName = storeName,
                            priceLabel = priceLabel,
                        )
                        navigator.push(
                            Route.ProductDetail(
                                productId = product.id,
                                slug = product.slug,
                            ),
                        )
                    },
                )
            }
        },
    )
}
