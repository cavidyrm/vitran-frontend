package com.vitran.shop.platform

import com.vitran.shop.core.platform.share.ExternalUrlLauncher
import kotlinx.browser.window

class JsExternalUrlLauncher : ExternalUrlLauncher {
    override fun open(url: String): Boolean {
        return try {
            window.open(url, "_blank")
            true
        } catch (_: Throwable) {
            false
        }
    }
}
