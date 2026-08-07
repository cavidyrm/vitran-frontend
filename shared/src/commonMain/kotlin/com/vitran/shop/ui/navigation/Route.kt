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
        -> false
        Route.Home,
        Route.Categories,
        Route.Offers,
        Route.Saved,
        Route.Account,
        -> true
    }
