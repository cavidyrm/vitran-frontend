// Same-origin proxies so browser fetch is not subject to API/CDN CORS.
// Merges into Kotlin-generated webpack config — see KotlinWebpack.configDirectory.
// Override the API host with VITRAN_API_ORIGIN (e.g. http://127.0.0.1:8080 for a
// local backend — webpack must then bind a different port than the API).
const apiOrigin = process.env.VITRAN_API_ORIGIN || "https://api.vitran.ir";

config.devServer = Object.assign({}, config.devServer || {}, {
    historyApiFallback: true,
    proxy: [
        {
            context: ["/api"],
            target: apiOrigin,
            changeOrigin: true,
            secure: apiOrigin.startsWith("https://"),
        },
        {
            context: ["/health"],
            target: apiOrigin,
            changeOrigin: true,
            secure: apiOrigin.startsWith("https://"),
        },
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
