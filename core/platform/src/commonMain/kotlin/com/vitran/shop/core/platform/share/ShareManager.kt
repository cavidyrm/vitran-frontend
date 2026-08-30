package com.vitran.shop.core.platform.share

/**
 * Platform share sheet / equivalent. Does not invent frontend URLs.
 *
 * @return true when the platform presented a share surface, false when unsupported.
 */
interface ShareManager {
    fun share(text: String, url: String? = null): Boolean
}
