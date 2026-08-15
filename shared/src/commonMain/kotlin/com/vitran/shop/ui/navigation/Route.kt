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
        -> false
        Route.Home,
        Route.Categories,
        Route.Offers,
        Route.Saved,
        Route.Account,
        -> true
    }

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
