// SPA History API fallback for webpack-dev-server (js + wasmJs browser runs).
// Direct paths like /offers must serve index.html so Navigation 3 can read pathname.
// Merges into Kotlin-generated webpack config — see KotlinWebpack.configDirectory.
config.devServer = Object.assign({}, config.devServer || {}, {
    historyApiFallback: true,
});
