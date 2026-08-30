package com.vitran.shop.platform

import com.vitran.shop.core.platform.share.ShareManager
import kotlinx.browser.window

class WasmShareManager : ShareManager {
    override fun share(text: String, url: String?): Boolean {
        val target = url?.takeIf { it.isNotBlank() } ?: return false
        return try {
            window.open(target, "_blank")
            true
        } catch (_: Throwable) {
            false
        }
    }
}
