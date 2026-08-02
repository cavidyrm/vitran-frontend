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
}
