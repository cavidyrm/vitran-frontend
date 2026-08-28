# Serves the Kotlin/Wasm web distribution with nginx.
# Build the app first (locally or in CI):
#   ./gradlew :webApp:wasmJsBrowserDistribution
FROM nginx:1.27-alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY webApp/build/dist/wasmJs/productionExecutable /usr/share/nginx/html

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=5s --retries=3 --start-period=10s \
    CMD wget -qO- http://127.0.0.1/ >/dev/null || exit 1
