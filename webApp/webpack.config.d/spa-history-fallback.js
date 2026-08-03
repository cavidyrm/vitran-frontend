// Proxy Shopify CDNs through webpack-dev-server so Coil/Ktor fetch on Wasm/JS
// is same-origin (Shopify does not send Access-Control-Allow-Origin).
// Merges into Kotlin-generated webpack config — see KotlinWebpack.configDirectory.
config.devServer = Object.assign({}, config.devServer || {}, {
    historyApiFallback: true,
    proxy: [
        {
            context: ["/shopify-assets-proxy"],
            target: "https://shopify-assets.shopifycdn.com",
            changeOrigin: true,
            secure: true,
            pathRewrite: { "^/shopify-assets-proxy": "" },
            headers: {
                // CDN returns 403 without a browser User-Agent.
                "User-Agent":
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36",
                Referer: "https://shop.app/",
                Origin: "https://shop.app",
            },
        },
        {
            context: ["/cdn-shopify-proxy"],
            target: "https://cdn.shopify.com",
            changeOrigin: true,
            secure: true,
            pathRewrite: { "^/cdn-shopify-proxy": "" },
            headers: {
                "User-Agent":
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36",
                Referer: "https://shop.app/",
                Origin: "https://shop.app",
            },
        },
    ],
});
