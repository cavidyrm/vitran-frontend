package com.vitran.shop.core.platform.storage

import kotlinx.browser.window

class LocalStorageWebStorageBackend : WebStorageBackend {
    override fun getItem(key: String): String? =
        runCatching { window.localStorage.getItem(key) }
            .getOrNull()
            ?.takeIf { it.isNotEmpty() }

    override fun setItem(key: String, value: String) {
        window.localStorage.setItem(key, value)
    }

    override fun removeItem(key: String) {
        window.localStorage.removeItem(key)
    }
}
