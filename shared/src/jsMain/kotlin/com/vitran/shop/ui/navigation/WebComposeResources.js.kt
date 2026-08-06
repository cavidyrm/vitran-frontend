package com.vitran.shop.ui.navigation

import org.jetbrains.compose.resources.configureWebResources

private var webComposeResourcesInitialized = false

actual fun initWebComposeResources() {
    if (webComposeResourcesInitialized) return
    webComposeResourcesInitialized = true
    configureWebResources {
        resourcePathMapping { path ->
            "/" + path.removePrefix("./").removePrefix("/")
        }
    }
}
