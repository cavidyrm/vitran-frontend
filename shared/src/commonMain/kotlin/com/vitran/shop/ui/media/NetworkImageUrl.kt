package com.vitran.shop.ui.media

/**
 * Rewrites remote image URLs when the platform cannot fetch CDN hosts directly
 * (browser CORS). Non-web targets return [url] unchanged.
 */
expect fun resolveNetworkImageUrl(url: String): String
