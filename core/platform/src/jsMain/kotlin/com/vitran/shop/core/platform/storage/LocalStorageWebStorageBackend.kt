package com.vitran.shop.core.platform.storage

class LocalStorageWebStorageBackend : WebStorageBackend {
    override fun getItem(key: String): String? =
        localStorageGetItem(key)?.takeIf { it.isNotEmpty() }

    override fun setItem(key: String, value: String) {
        localStorageSetItem(key, value)
    }

    override fun removeItem(key: String) {
        localStorageRemoveItem(key)
    }
}

@Suppress("UNUSED_PARAMETER")
private fun localStorageGetItem(key: String): String? =
    js("window.localStorage.getItem(key)") as String?

@Suppress("UNUSED_PARAMETER")
private fun localStorageSetItem(key: String, value: String) {
    js("window.localStorage.setItem(key, value)")
}

@Suppress("UNUSED_PARAMETER")
private fun localStorageRemoveItem(key: String) {
    js("window.localStorage.removeItem(key)")
}
