package com.vitran.shop.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlin.js.ExperimentalWasmJsInterop
import web.events.EventHandler
import web.events.addHandler
import web.history.PopStateEvent
import web.history.history
import web.location.location
import web.window.popStateEvent
import web.window.window

@OptIn(ExperimentalWasmJsInterop::class)
@Composable
actual fun rememberInitialRoute(fallback: Route): Route =
    remember(fallback) {
        RouteMapper.fromPath(location.pathname) ?: fallback
    }

@OptIn(ExperimentalWasmJsInterop::class)
@Composable
actual fun BindBrowserNavigation(
    navState: NavigationState,
    navigator: Navigator,
) {
    var lastWrittenPath by remember {
        mutableStateOf(RouteMapper.toPath(navState.currentRoute))
    }
    var applyingFromBrowser by remember { mutableStateOf(false) }

    // Align History with the seeded route without adding a history entry.
    LaunchedEffect(Unit) {
        val path = RouteMapper.toPath(navState.currentRoute)
        if (location.pathname != path) {
            history.replaceState(null, "", path)
        }
        lastWrittenPath = path
    }

    // NavigationState → URL
    LaunchedEffect(navState.currentRoute, navState.urlSyncMode) {
        val path = RouteMapper.toPath(navState.currentRoute)
        if (applyingFromBrowser) {
            lastWrittenPath = path
            return@LaunchedEffect
        }
        when (navState.urlSyncMode) {
            UrlSyncMode.HistoryBack -> {
                // Stack already popped; rewind browser History to match.
                if (location.pathname != path) {
                    lastWrittenPath = path
                    history.back()
                } else {
                    lastWrittenPath = path
                }
            }
            UrlSyncMode.Replace -> {
                if (path != lastWrittenPath) {
                    history.replaceState(null, "", path)
                    lastWrittenPath = path
                }
            }
            UrlSyncMode.Push -> {
                if (path != lastWrittenPath) {
                    history.pushState(null, "", path)
                    lastWrittenPath = path
                }
            }
        }
    }

    // Browser → NavigationState (back/forward + address-bar driven history)
    DisposableEffect(navigator, navState) {
        val unsubscribe = window.popStateEvent.addHandler(
            EventHandler { _: PopStateEvent ->
                val path = location.pathname
                val route = RouteMapper.fromPath(path) ?: return@EventHandler
                if (route == navState.currentRoute) {
                    lastWrittenPath = path
                    return@EventHandler
                }
                applyingFromBrowser = true
                // Pop children until the stack matches the browser path, else replace.
                while (navState.backStack.size > 1 && navState.currentRoute != route) {
                    navState.backStack.removeLastOrNull()
                }
                if (navState.currentRoute != route) {
                    navState.urlSyncMode = UrlSyncMode.Replace
                    navState.backStack.clear()
                    navState.backStack.add(route)
                }
                lastWrittenPath = RouteMapper.toPath(navState.currentRoute)
                applyingFromBrowser = false
            },
        )
        onDispose(unsubscribe)
    }
}
