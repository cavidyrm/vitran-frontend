package com.vitran.shop.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.vitran.shop.feature.auth.domain.usecase.LogoutUseCase
import com.vitran.shop.ui.screens.AccountCitiesScreen
import com.vitran.shop.ui.screens.AccountCityCreateScreen
import com.vitran.shop.ui.screens.AccountCityDetailScreen
import com.vitran.shop.ui.screens.AccountScreen
import com.vitran.shop.ui.screens.AccountSettingsScreen
import com.vitran.shop.ui.screens.AccountUserDetailScreen
import com.vitran.shop.ui.screens.AccountUsersScreen
import com.vitran.shop.ui.screens.AdminPlansScreen
import com.vitran.shop.ui.screens.AboutScreen
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
import com.vitran.shop.ui.screens.SearchResultsScreen
import com.vitran.shop.ui.screens.StorePlanScreen
import com.vitran.shop.ui.screens.StorePlanUpgradeScreen
import com.vitran.shop.ui.screens.StoreScreen
import com.vitran.shop.ui.components.SiteFooterLinkId
import com.vitran.shop.ui.sections.account.AccountDest
import com.vitran.shop.ui.sections.product.MockProductCatalog
import com.vitran.shop.ui.sections.product.productSlug
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

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
    val logoutUseCase: LogoutUseCase = koinInject()
    val signOutScope = rememberCoroutineScope()
    val onSignOut: () -> Unit = {
        signOutScope.launch {
            logoutUseCase()
        }
        navigator.navigate(Route.Login)
    }
    val onFooterLink: (SiteFooterLinkId) -> Unit = { id ->
        navigator.handleSiteFooterLink(navState, id)
    }
    val onSearchSubmit: (String) -> Unit = { query ->
        if (query.isNotBlank()) {
            navigator.push(Route.Search(query = query.trim()))
        }
    }
    val onProductOpen: (
        String,
        String,
        String,
        String,
        String,
    ) -> Unit = { id, title, _, _, _ ->
        navigator.push(
            Route.ProductDetail(
                productId = id,
                slug = productSlug(title),
            ),
        )
    }
    NavDisplay(
        backStack = navState.backStack,
        modifier = modifier,
        onBack = { navigator.goBack() },
        entryProvider = entryProvider {
            entry<Route.Home> {
                HomeScreen(
                    onProductOpen = onProductOpen,
                    onStoreOpen = { shopId ->
                        navigator.push(Route.Store(shopId = shopId))
                    },
                    onSearchSubmit = onSearchSubmit,
                    onFooterLinkClick = onFooterLink,
                )
            }
            entry<Route.Categories> {
                CategoriesScreen(
                    onProductOpen = onProductOpen,
                    onStoreOpen = { shopId ->
                        navigator.push(Route.Store(shopId = shopId))
                    },
                    onSearchSubmit = onSearchSubmit,
                    onFooterLinkClick = onFooterLink,
                    onLoginRequest = { navigator.navigate(Route.Login) },
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
                    onOpenAdminPlans = { navigator.push(Route.AdminPlans) },
                    onSignOut = onSignOut,
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
                    onFooterLinkClick = onFooterLink,
                )
            }
            entry<Route.Profile> {
                ProfileScreen(
                    onBack = { navigator.goBack() },
                    onDestClick = { dest -> navigator.openAccountDest(navState, dest) },
                    onOpenSaved = { navigator.navigate(Route.Saved) },
                    onFooterLinkClick = onFooterLink,
                )
            }
            entry<Route.Referrals> {
                ReferralsScreen(
                    onBack = { navigator.goBack() },
                    onDestClick = { dest -> navigator.openAccountDest(navState, dest) },
                    onOpenSaved = { navigator.navigate(Route.Saved) },
                    onFooterLinkClick = onFooterLink,
                )
            }
            entry<Route.Following> {
                FollowingScreen(
                    onBack = { navigator.goBack() },
                    onDestClick = { dest -> navigator.openAccountDest(navState, dest) },
                    onOpenSaved = { navigator.navigate(Route.Saved) },
                    onStoreOpen = { shopId -> navigator.push(Route.Store(shopId = shopId)) },
                    onFooterLinkClick = onFooterLink,
                )
            }
            entry<Route.AccountSettings> {
                AccountSettingsScreen(
                    onBack = { navigator.goBack() },
                    onDestClick = { dest -> navigator.openAccountDest(navState, dest) },
                    onOpenSaved = { navigator.navigate(Route.Saved) },
                    onOpenProfile = { navigator.openAccountDest(navState, AccountDest.Profile) },
                    onSignOut = onSignOut,
                    onFooterLinkClick = onFooterLink,
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
                    onFooterLinkClick = onFooterLink,
                )
            }
            entry<Route.AccountUserDetail> { key ->
                AccountUserDetailScreen(
                    userId = key.userId,
                    onBack = { navigator.goBack() },
                    onDestClick = { dest -> navigator.openAccountDest(navState, dest) },
                    onOpenSaved = { navigator.navigate(Route.Saved) },
                    onFooterLinkClick = onFooterLink,
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
                    onFooterLinkClick = onFooterLink,
                )
            }
            entry<Route.AccountCityCreate> {
                AccountCityCreateScreen(
                    onBack = { navigator.goBack() },
                    onDestClick = { dest -> navigator.openAccountDest(navState, dest) },
                    onOpenSaved = { navigator.navigate(Route.Saved) },
                    onFooterLinkClick = onFooterLink,
                )
            }
            entry<Route.AccountCityDetail> { key ->
                AccountCityDetailScreen(
                    cityId = key.cityId,
                    onBack = { navigator.goBack() },
                    onDestClick = { dest -> navigator.openAccountDest(navState, dest) },
                    onOpenSaved = { navigator.navigate(Route.Saved) },
                    onFooterLinkClick = onFooterLink,
                )
            }
            entry<Route.Login> {
                LoginScreen(
                    showPasswordResetNotice = passwordResetNotice,
                    onPasswordResetNoticeConsumed = { passwordResetNotice = false },
                    onCreateAccount = { navigator.push(Route.Register) },
                    onForgotPassword = { navigator.push(Route.ForgotPassword) },
                    onSignedIn = { navigator.navigate(Route.Home) },
                    onVerificationRequired = {
                        navigator.push(Route.RegisterVerify(phone = ""))
                    },
                )
            }
            entry<Route.Register> {
                RegisterScreen(
                    onContinue = { phone ->
                        navigator.push(Route.RegisterVerify(phone = phone))
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
                        navigator.navigate(Route.Home)
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
            entry<Route.AdminPlans> {
                AdminPlansScreen(
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
                    onProductOpen = onProductOpen,
                    onStoreOpen = { shopId ->
                        navigator.push(Route.Store(shopId = shopId))
                    },
                    onSearchSubmit = onSearchSubmit,
                    onFooterLinkClick = onFooterLink,
                    onLoginRequest = { navigator.navigate(Route.Login) },
                )
            }
            entry<Route.Store> { key ->
                StoreScreen(
                    shopId = key.shopId,
                    onProductOpen = onProductOpen,
                    onSearchSubmit = onSearchSubmit,
                    onFooterLinkClick = onFooterLink,
                    onLoginRequest = { navigator.navigate(Route.Login) },
                )
            }
            entry<Route.Search> { key ->
                SearchResultsScreen(
                    query = key.query,
                    onProductOpen = onProductOpen,
                )
            }
            entry<Route.About> {
                AboutScreen(
                    onHomeClick = { navigator.navigate(Route.Home) },
                    onCreateStore = { navigator.push(Route.CreateStore) },
                    onFooterLinkClick = onFooterLink,
                )
            }
        },
    )
}

private fun Navigator.handleSiteFooterLink(state: NavigationState, id: SiteFooterLinkId) {
    when (id) {
        SiteFooterLinkId.About -> {
            if (state.currentRoute is Route.About) return
            push(Route.About)
        }
        SiteFooterLinkId.BuildStore,
        SiteFooterLinkId.StartSellingFree,
        -> push(Route.CreateStore)
        else -> Unit
    }
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
