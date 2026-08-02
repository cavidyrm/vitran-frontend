package com.vitran.shop.ui.navigation

import androidx.compose.runtime.Composable

/**
 * Platform start route for [rememberNavigationState].
 *
 * Web: from `window.location.pathname` (e.g. `/offers` → [Route.Offers]).
 * Other platforms: [fallback] today; later Intent / Universal Link / CLI entry points.
 */
@Composable
expect fun rememberInitialRoute(fallback: Route = Route.Home): Route

/**
 * Bidirectional, loop-safe sync between [NavigationState] and browser History pathname.
 * No-op on non-web targets. Never hardcodes a domain/host.
 */
@Composable
expect fun BindBrowserNavigation(
    navState: NavigationState,
    navigator: Navigator,
)
