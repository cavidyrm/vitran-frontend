package com.vitran.shop.platform

import com.vitran.shop.core.platform.share.ShareManager
import kotlinx.browser.window

class JsShareManager : ShareManager {
    override fun share(text: String, url: String?): Boolean {
        val body = if (url.isNullOrBlank()) text else "$text\n$url"
        return try {
            val navigator = window.navigator.asDynamic()
            val shareFn = navigator.share
            if (shareFn != null) {
                val payload = js("{}")
                payload.text = body
                if (!url.isNullOrBlank()) payload.url = url
                shareFn.call(navigator, payload)
                true
            } else {
                window.asDynamic().prompt("اشتراک‌گذاری", body)
                true
            }
        } catch (_: Throwable) {
            false
        }
    }
}
