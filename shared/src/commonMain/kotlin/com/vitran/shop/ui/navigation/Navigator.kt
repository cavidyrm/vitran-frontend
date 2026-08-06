package com.vitran.shop.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * How the next route change should sync to browser History (web only).
 */
enum class UrlSyncMode {
    /** Push a new history entry (default for forward navigation). */
    Push,

    /** Replace the current history entry (top-level tab switch). */
    Replace,

    /**
     * App back popped a child — browser should `history.back()` so History and
     * [NavBackStack][androidx.navigation3.runtime.NavBackStack] stay aligned.
     */
    HistoryBack,
}

/**
 * Navigation actions over [NavigationState].
 *
 * Top-level tabs use [navigate] (single root). Child screens (e.g. product detail)
 * use [push] / [goBack].
 */
interface Navigator {
    fun navigate(route: Route)
    fun push(route: Route)
    fun goBack()
}

private class DefaultNavigator(
    private val state: NavigationState,
) : Navigator {
    override fun navigate(route: Route) {
        if (state.currentRoute == route && state.backStack.size == 1) return
        state.urlSyncMode = UrlSyncMode.Replace
        // Top-level: keep exactly one root — no stacked sibling tabs.
        state.backStack.clear()
        state.backStack.add(route)
    }

    override fun push(route: Route) {
        if (state.currentRoute == route) return
        state.urlSyncMode = UrlSyncMode.Push
        state.backStack.add(route)
    }

    override fun goBack() {
        if (state.backStack.size > 1) {
            state.urlSyncMode = UrlSyncMode.HistoryBack
            state.backStack.removeLastOrNull()
        }
    }
}

@Composable
fun rememberNavigator(state: NavigationState): Navigator =
    remember(state) { DefaultNavigator(state) }
