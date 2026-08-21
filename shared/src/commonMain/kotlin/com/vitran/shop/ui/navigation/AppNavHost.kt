package com.vitran.shop.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.vitran.shop.ui.screens.AccountCitiesScreen
import com.vitran.shop.ui.screens.AccountCityCreateScreen
import com.vitran.shop.ui.screens.AccountCityDetailScreen
import com.vitran.shop.ui.screens.AccountScreen
import com.vitran.shop.ui.screens.AccountSettingsScreen
import com.vitran.shop.ui.screens.AccountUserDetailScreen
import com.vitran.shop.ui.screens.AccountUsersScreen
import com.vitran.shop.ui.screens.CategoriesScreen
import com.vitran.shop.ui.screens.CreateCategoryScreen
import com.vitran.shop.ui.screens.CreateProductScreen
import com.vitran.shop.ui.screens.CreateStoreScreen
import com.vitran.shop.ui.screens.FollowingScreen
import com.vitran.shop.ui.screens.ForgotPasswordScreen
import com.vitran.shop.ui.screens.HomeScreen
import com.vitran.shop.ui.screens.LoginScreen
import com.vitran.shop.ui.screens.OffersScreen
import com.vitran.shop.ui.screens.ProductDetailScreen
import com.vitran.shop.ui.screens.ProfileScreen
import com.vitran.shop.ui.screens.ReferralsScreen
import com.vitran.shop.ui.screens.RegisterScreen
import com.vitran.shop.ui.screens.RegisterVerifyScreen
import com.vitran.shop.ui.screens.ResetPasswordScreen
import com.vitran.shop.ui.screens.SavedScreen
import com.vitran.shop.ui.screens.StorePlanScreen
import com.vitran.shop.ui.screens.StorePlanUpgradeScreen
import com.vitran.shop.ui.screens.StoreScreen
import com.vitran.shop.ui.sections.account.AccountDest
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
    var passwordResetNotice by remember { mutableStateOf(false) }
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
                    onStoreOpen = { shopId ->
                        navigator.push(Route.Store(shopId = shopId))
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
                    onStoreOpen = { shopId ->
                        navigator.push(Route.Store(shopId = shopId))
                    },
                )
            }
            entry<Route.Offers> { OffersScreen() }
            entry<Route.Saved> { SavedScreen() }
            entry<Route.Account> {
                AccountScreen(
                    onDestClick = { dest -> navigator.openAccountDest(navState, dest) },
                    onOpenProfile = { navigator.push(Route.Profile) },
                    onOpenSaved = { navigator.navigate(Route.Saved) },
                    onOpenFollowing = { navigator.push(Route.Following) },
                    onOpenReferrals = { navigator.push(Route.Referrals) },
                    onCreateStore = { navigator.push(Route.CreateStore) },
                    onOpenStorePlan = { navigator.push(Route.StorePlan) },
                    onSignOut = { navigator.push(Route.Login) },
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
            entry<Route.Profile> {
                ProfileScreen(
                    onBack = { navigator.goBack() },
                    onDestClick = { dest -> navigator.openAccountDest(navState, dest) },
                    onOpenSaved = { navigator.navigate(Route.Saved) },
                )
            }
            entry<Route.Referrals> {
                ReferralsScreen(
                    onBack = { navigator.goBack() },
                    onDestClick = { dest -> navigator.openAccountDest(navState, dest) },
                    onOpenSaved = { navigator.navigate(Route.Saved) },
                )
            }
            entry<Route.Following> {
                FollowingScreen(
                    onBack = { navigator.goBack() },
                    onDestClick = { dest -> navigator.openAccountDest(navState, dest) },
                    onOpenSaved = { navigator.navigate(Route.Saved) },
                    onStoreOpen = { shopId -> navigator.push(Route.Store(shopId = shopId)) },
                )
            }
            entry<Route.AccountSettings> {
                AccountSettingsScreen(
                    onBack = { navigator.goBack() },
                    onDestClick = { dest -> navigator.openAccountDest(navState, dest) },
                    onOpenSaved = { navigator.navigate(Route.Saved) },
                    onOpenProfile = { navigator.openAccountDest(navState, AccountDest.Profile) },
                    onSignOut = { navigator.push(Route.Login) },
                )
            }
            entry<Route.AccountUsers> {
                AccountUsersScreen(
                    onBack = { navigator.goBack() },
                    onDestClick = { dest -> navigator.openAccountDest(navState, dest) },
                    onOpenSaved = { navigator.navigate(Route.Saved) },
                    onUserOpen = { userId ->
                        navigator.push(Route.AccountUserDetail(userId = userId.toString()))
                    },
                )
            }
            entry<Route.AccountUserDetail> { key ->
                AccountUserDetailScreen(
                    userId = key.userId,
                    onBack = { navigator.goBack() },
                    onDestClick = { dest -> navigator.openAccountDest(navState, dest) },
                    onOpenSaved = { navigator.navigate(Route.Saved) },
                )
            }
            entry<Route.AccountCities> {
                AccountCitiesScreen(
                    onBack = { navigator.goBack() },
                    onDestClick = { dest -> navigator.openAccountDest(navState, dest) },
                    onOpenSaved = { navigator.navigate(Route.Saved) },
                    onCityOpen = { cityId ->
                        navigator.push(Route.AccountCityDetail(cityId = cityId.toString()))
                    },
                    onAddCity = { navigator.push(Route.AccountCityCreate) },
                )
            }
            entry<Route.AccountCityCreate> {
                AccountCityCreateScreen(
                    onBack = { navigator.goBack() },
                    onDestClick = { dest -> navigator.openAccountDest(navState, dest) },
                    onOpenSaved = { navigator.navigate(Route.Saved) },
                )
            }
            entry<Route.AccountCityDetail> { key ->
                AccountCityDetailScreen(
                    cityId = key.cityId,
                    onBack = { navigator.goBack() },
                    onDestClick = { dest -> navigator.openAccountDest(navState, dest) },
                    onOpenSaved = { navigator.navigate(Route.Saved) },
                )
            }
            entry<Route.Login> {
                LoginScreen(
                    showPasswordResetNotice = passwordResetNotice,
                    onPasswordResetNoticeConsumed = { passwordResetNotice = false },
                    onCreateAccount = { navigator.push(Route.Register) },
                    onForgotPassword = { navigator.push(Route.ForgotPassword) },
                )
            }
            entry<Route.Register> {
                RegisterScreen(
                    onContinue = { credentials ->
                        navigator.push(Route.RegisterVerify(phone = credentials.mobile.trim()))
                    },
                    onSignIn = {
                        if (navState.backStack.size > 1) {
                            navigator.goBack()
                        } else {
                            navigator.navigate(Route.Login)
                        }
                    },
                )
            }
            entry<Route.RegisterVerify> { key ->
                RegisterVerifyScreen(
                    phone = key.phone,
                    onChangeMobile = {
                        if (navState.backStack.size > 1) {
                            navigator.goBack()
                        } else {
                            // Deep link to verify only — replace with register form.
                            navigator.navigate(Route.Register)
                        }
                    },
                    onVerified = {
                        // Mock phase — no auth state change.
                    },
                )
            }
            entry<Route.ForgotPassword> {
                ForgotPasswordScreen(
                    onSendCode = { phone ->
                        navigator.push(Route.ResetPassword(phone = phone))
                    },
                    onBackToLogin = {
                        if (navState.backStack.size > 1) {
                            navigator.goBack()
                        } else {
                            navigator.navigate(Route.Login)
                        }
                    },
                )
            }
            entry<Route.ResetPassword> { key ->
                ResetPasswordScreen(
                    phone = key.phone,
                    onChangeMobile = {
                        if (navState.backStack.size > 1) {
                            navigator.goBack()
                        } else {
                            navigator.navigate(Route.ForgotPassword)
                        }
                    },
                    onResetComplete = {
                        // Mock phase — no auth state change.
                        passwordResetNotice = true
                        navigator.navigate(Route.Login)
                    },
                )
            }
            entry<Route.CreateStore> {
                CreateStoreScreen(
                    onBack = { navigator.goBack() },
                    onViewStore = { shopId -> navigator.push(Route.Store(shopId = shopId)) },
                    onAddProduct = { navigator.push(Route.CreateProduct) },
                )
            }
            entry<Route.StorePlan> {
                StorePlanScreen(
                    onBack = { navigator.goBack() },
                    onUpgradeClick = { navigator.push(Route.StorePlanUpgrade) },
                )
            }
            entry<Route.StorePlanUpgrade> {
                StorePlanUpgradeScreen(
                    onBack = { navigator.goBack() },
                )
            }
            entry<Route.CreateProduct> {
                CreateProductScreen(
                    onBack = { navigator.goBack() },
                )
            }
            entry<Route.CreateCategory> {
                CreateCategoryScreen(
                    onBack = { navigator.goBack() },
                )
            }
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
                    onStoreOpen = { shopId ->
                        navigator.push(Route.Store(shopId = shopId))
                    },
                )
            }
            entry<Route.Store> { key ->
                StoreScreen(
                    shopId = key.shopId,
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

private fun Navigator.openAccountDest(state: NavigationState, dest: AccountDest) {
    val target: Route = when (dest) {
        AccountDest.Hub -> Route.Account
        AccountDest.Profile -> Route.Profile
        AccountDest.Referrals -> Route.Referrals
        AccountDest.Following -> Route.Following
        AccountDest.Settings -> Route.AccountSettings
        AccountDest.Users -> Route.AccountUsers
        AccountDest.Cities -> Route.AccountCities
    }
    if (state.currentRoute == target) return
    val nestedList = when (state.currentRoute) {
        is Route.AccountUserDetail -> Route.AccountUsers
        is Route.AccountCityDetail, Route.AccountCityCreate -> Route.AccountCities
        else -> null
    }
    if (nestedList != null && state.backStack.size > 1) {
        goBack()
        if (target == nestedList) return
    }
    if (target == Route.Account) {
        if (state.backStack.size > 1) goBack() else navigate(Route.Account)
        return
    }
    if (state.currentRoute.isAccountChild() && state.backStack.size > 1) {
        goBack()
    }
    push(target)
}
