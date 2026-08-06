package com.vitran.shop.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

/**
 * Single source of truth for navigation: Navigation 3 [NavBackStack].
 *
 * Chrome and screens must only read [currentRoute] / [chromeRoute] and request
 * changes via [Navigator].
 */
class NavigationState(
    val backStack: NavBackStack<NavKey>,
) {
    /**
     * Active route. Stack is always seeded; fails loudly if the invariant is broken.
     */
    val currentRoute: Route
        get() = requireNotNull(backStack.lastOrNull() as? Route) {
            "Navigation back stack is empty or contains a non-Route key"
        }

    /**
     * Last top-level route in the stack — used for side/bottom nav selection while
     * a child (e.g. [Route.ProductDetail]) is showing.
     */
    val chromeRoute: Route
        get() {
            for (i in backStack.lastIndex downTo 0) {
                val route = backStack[i] as? Route ?: continue
                if (route.isTopLevel()) return route
            }
            return Route.Home
        }

    /** Hint for web History sync; consumed by [BindBrowserNavigation]. */
    var urlSyncMode: UrlSyncMode by mutableStateOf(UrlSyncMode.Replace)
}

private val routeSavedStateConfiguration: SavedStateConfiguration =
    SavedStateConfiguration {
        serializersModule = SerializersModule {
            polymorphic(baseClass = NavKey::class) {
                subclass(serializer = Route.Home.serializer())
                subclass(serializer = Route.Categories.serializer())
                subclass(serializer = Route.Offers.serializer())
                subclass(serializer = Route.Saved.serializer())
                subclass(serializer = Route.Account.serializer())
                subclass(serializer = Route.ProductDetail.serializer())
            }
        }
    }

@Composable
fun rememberNavigationState(start: Route = Route.Home): NavigationState {
    val backStack = rememberNavBackStack(routeSavedStateConfiguration, start)
    return remember(backStack) { NavigationState(backStack) }
}
