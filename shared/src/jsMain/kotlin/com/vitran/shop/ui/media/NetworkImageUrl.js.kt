package com.vitran.shop.ui.media

import kotlin.js.ExperimentalWasmJsInterop
import web.location.location

private const val ShopifyAssetsHost = "https://shopify-assets.shopifycdn.com"
private const val ShopifyCdnHost = "https://cdn.shopify.com"
private const val ShopifyAssetsProxy = "/shopify-assets-proxy"
private const val ShopifyCdnProxy = "/cdn-shopify-proxy"

/**
 * Absolute same-origin URLs for webpack-dev-server proxies.
 * Coil's NetworkFetcher only accepts `http`/`https` schemes — relative
 * `/proxy/...` paths are ignored (no fetch, placeholder-only, no console error).
 */
@OptIn(ExperimentalWasmJsInterop::class)
actual fun resolveNetworkImageUrl(url: String): String {
    val path = when {
        url.startsWith(ShopifyAssetsHost) ->
            ShopifyAssetsProxy + url.removePrefix(ShopifyAssetsHost)
        url.startsWith(ShopifyCdnHost) ->
            ShopifyCdnProxy + url.removePrefix(ShopifyCdnHost)
        else -> return url
    }
    return location.origin + path
}
