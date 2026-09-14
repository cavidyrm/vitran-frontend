package com.vitran.shop.ui.media

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NetworkImageUrlRewriteTest {

    @Test
    fun shopifyHosts_rewriteToProxies() {
        assertEquals(
            "/shopify-assets-proxy/shop-assets/x.png",
            rewriteNetworkImagePathForWeb("https://shopify-assets.shopifycdn.com/shop-assets/x.png"),
        )
        assertEquals(
            "/cdn-shopify-proxy/s/files/1.png",
            rewriteNetworkImagePathForWeb("https://cdn.shopify.com/s/files/1.png"),
        )
    }

    @Test
    fun arvanStorage_rewritesHostIntoProxyPath() {
        assertEquals(
            "/arvanstorage-proxy/hot.ir-central1.arvanstorage.ir/vitran/users/2/a.jpg",
            rewriteNetworkImagePathForWeb(
                "https://hot.ir-central1.arvanstorage.ir/vitran/users/2/a.jpg",
            ),
        )
    }

    @Test
    fun unrelatedHosts_unchanged() {
        assertNull(rewriteNetworkImagePathForWeb("https://cdn.example/avatar.png"))
        assertNull(rewriteNetworkImagePathForWeb("https://evil.example/arvanstorage.ir/x.jpg"))
    }
}
