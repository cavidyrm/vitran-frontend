package com.vitran.shop.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * Navigation actions over [NavigationState].
 *
 * Today the app has only top-level destinations: [navigate] keeps exactly one root entry.
 * Later, the same abstraction must support Root → Child → Child without changing AppShell/chrome.
 */
interface Navigator {
    fun navigate(route: Route)
    fun goBack()
}

private class DefaultNavigator(
    private val state: NavigationState,
) : Navigator {
    override fun navigate(route: Route) {
        if (state.currentRoute == route) return
        // Top-level only: keep exactly one root — no stacked sibling tabs.
        state.backStack.clear()
        state.backStack.add(route)
    }

    override fun goBack() {
        if (state.backStack.size > 1) {
            state.backStack.removeLastOrNull()
        }
    }
}

@Composable
fun rememberNavigator(state: NavigationState): Navigator =
    remember(state) { DefaultNavigator(state) }
