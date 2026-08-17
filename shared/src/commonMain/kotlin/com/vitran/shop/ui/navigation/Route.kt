package com.vitran.shop.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Stable, serializable app routes for Navigation 3.
 *
 * Implementations must remain `@Serializable` named types — never anonymous or
 * runtime-only keys — so back-stack persistence and restore work across platforms.
 */
@Serializable
sealed interface Route : NavKey {
    @Serializable
    data object Home : Route

    @Serializable
    data object Categories : Route

    @Serializable
    data object Offers : Route

    @Serializable
    data object Saved : Route

    @Serializable
    data object Account : Route

    /**
     * Profile editor — path `/account/profile`.
     * Child of Account; shopper chrome stays, Account tab remains selected.
     */
    @Serializable
    data object Profile : Route

    /** Referral dashboard — path `/account/referrals`. Child of Account. */
    @Serializable
    data object Referrals : Route

    /** Followed stores — path `/account/following`. Child of Account. */
    @Serializable
    data object Following : Route

    /** Account settings — path `/account/settings`. Child of Account. */
    @Serializable
    data object AccountSettings : Route

    /** User management list — path `/account/users`. Child of Account. */
    @Serializable
    data object AccountUsers : Route

    /**
     * User detail — path `/account/users/{userId}`.
     * Child of Account; pushed onto the users list; shopper chrome stays.
     */
    @Serializable
    data class AccountUserDetail(
        val userId: String,
    ) : Route

    /** City management list — path `/account/cities`. Child of Account. */
    @Serializable
    data object AccountCities : Route

    /** Add city — path `/account/cities/new`. Child of Account. */
    @Serializable
    data object AccountCityCreate : Route

    /**
     * City detail — path `/account/cities/{cityId}`.
     * Child of Account; pushed onto the cities list; shopper chrome stays.
     */
    @Serializable
    data class AccountCityDetail(
        val cityId: String,
    ) : Route

    /**
     * Sign-in — path `/account/login`.
     * Mobile + password only (no OTP). Child route; no app chrome while showing.
     */
    @Serializable
    data object Login : Route

    /**
     * Register — path `/account/register`.
     * Mobile + password step. Child route; no app chrome while showing.
     */
    @Serializable
    data object Register : Route

    /**
     * Register OTP verify — path `/account/register/verify`.
     * [phone] is carried on the nav key for in-app push; bare deep links use empty phone.
     */
    @Serializable
    data class RegisterVerify(
        val phone: String = "",
    ) : Route

    /**
     * Forgot password — path `/account/forgot`.
     * Mobile only; submit continues to reset. Child route; no app chrome while showing.
     */
    @Serializable
    data object ForgotPassword : Route

    /**
     * Reset password — path `/account/forgot/reset`.
     * OTP + new password. [phone] is carried on the nav key; bare deep links use empty phone.
     */
    @Serializable
    data class ResetPassword(
        val phone: String = "",
    ) : Route

    /**
     * Merchant admin — create store. Path `/admin/stores/new`.
     * Child route: pushed onto the stack; no shopper chrome while showing.
     */
    @Serializable
    data object CreateStore : Route

    /**
     * Merchant admin — add product. Path `/admin/products/new`.
     * Child route: pushed onto the stack; no shopper chrome while showing.
     */
    @Serializable
    data object CreateProduct : Route

    /**
     * Merchant admin — pick a Standard Product Taxonomy node.
     * Path `/admin/categories/new`. Child route; no shopper chrome while showing.
     */
    @Serializable
    data object CreateCategory : Route

    /**
     * Product detail — shop.app path `/products/{productId}/{slug}`.
     * Child route: pushed onto the stack; does not replace the chrome tab root.
     */
    @Serializable
    data class ProductDetail(
        val productId: String,
        val slug: String,
    ) : Route

    /**
     * Store / merchant page — shop.app path `/m/{shopId}`.
     * Child route: pushed onto the stack; does not replace the chrome tab root.
     */
    @Serializable
    data class Store(
        val shopId: String,
    ) : Route
}

/** Top-level tab destinations shown in app chrome (side / bottom nav). */
fun Route.isTopLevel(): Boolean =
    when (this) {
        is Route.ProductDetail,
        is Route.Store,
        is Route.RegisterVerify,
        is Route.ResetPassword,
        Route.Login,
        Route.Register,
        Route.ForgotPassword,
        Route.CreateStore,
        Route.CreateProduct,
        Route.CreateCategory,
        Route.Profile,
        Route.Referrals,
        Route.Following,
        Route.AccountSettings,
        Route.AccountUsers,
        is Route.AccountUserDetail,
        Route.AccountCities,
        Route.AccountCityCreate,
        is Route.AccountCityDetail,
        -> false
        Route.Home,
        Route.Categories,
        Route.Offers,
        Route.Saved,
        Route.Account,
        -> true
    }

fun Route.isAccountChild(): Boolean =
    this == Route.Profile ||
        this == Route.Referrals ||
        this == Route.Following ||
        this == Route.AccountSettings ||
        this == Route.AccountUsers ||
        this is Route.AccountUserDetail ||
        this == Route.AccountCities ||
        this == Route.AccountCityCreate ||
        this is Route.AccountCityDetail

/** Full-bleed destinations that hide shopper side / bottom nav. */
fun Route.hidesChrome(): Boolean =
    this is Route.Login ||
        this is Route.Register ||
        this is Route.RegisterVerify ||
        this is Route.ForgotPassword ||
        this is Route.ResetPassword ||
        this is Route.CreateStore ||
        this is Route.CreateProduct ||
        this is Route.CreateCategory
