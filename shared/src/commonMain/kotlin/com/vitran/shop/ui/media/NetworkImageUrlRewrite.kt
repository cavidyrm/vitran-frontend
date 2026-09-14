package com.vitran.shop.ui.media

private const val ShopifyAssetsHost = "https://shopify-assets.shopifycdn.com"
private const val ShopifyCdnHost = "https://cdn.shopify.com"
private const val ShopifyAssetsProxy = "/shopify-assets-proxy"
private const val ShopifyCdnProxy = "/cdn-shopify-proxy"
private const val ArvanStorageProxy = "/arvanstorage-proxy"

/**
 * Same-origin proxy path for browser targets, or null when [url] needs no rewrite.
 *
 * Arvan object storage does not send CORS headers; Coil's web NetworkFetcher uses
 * `fetch`, so those hosts must be proxied. Only `*.arvanstorage.ir` hosts are allowed.
 */
internal fun rewriteNetworkImagePathForWeb(url: String): String? {
    when {
        url.startsWith(ShopifyAssetsHost) ->
            return ShopifyAssetsProxy + url.removePrefix(ShopifyAssetsHost)
        url.startsWith(ShopifyCdnHost) ->
            return ShopifyCdnProxy + url.removePrefix(ShopifyCdnHost)
    }
    return rewriteArvanStoragePath(url)
}

private fun rewriteArvanStoragePath(url: String): String? {
    if (!url.startsWith("https://")) return null
    val withoutScheme = url.removePrefix("https://")
    val slash = withoutScheme.indexOf('/')
    val host = if (slash >= 0) withoutScheme.substring(0, slash) else withoutScheme
    if (!host.endsWith(".arvanstorage.ir")) return null
    val pathAndQuery = if (slash >= 0) withoutScheme.substring(slash) else ""
    return "$ArvanStorageProxy/$host$pathAndQuery"
}
