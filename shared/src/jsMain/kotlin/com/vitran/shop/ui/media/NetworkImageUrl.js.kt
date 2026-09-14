package com.vitran.shop.ui.media

import kotlin.js.ExperimentalWasmJsInterop
import web.location.location

/**
 * Absolute same-origin URLs for webpack-dev-server / nginx proxies.
 * Coil's NetworkFetcher only accepts `http`/`https` schemes — relative
 * `/proxy/...` paths are ignored (no fetch, placeholder-only, no console error).
 */
@OptIn(ExperimentalWasmJsInterop::class)
actual fun resolveNetworkImageUrl(url: String): String {
    val path = rewriteNetworkImagePathForWeb(url) ?: return url
    return location.origin + path
}
