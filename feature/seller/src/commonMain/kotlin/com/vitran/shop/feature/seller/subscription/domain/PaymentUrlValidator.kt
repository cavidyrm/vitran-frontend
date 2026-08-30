package com.vitran.shop.feature.seller.subscription.domain

/**
 * Validates payment handoff URLs before launching via ExternalUrlLauncher.
 */
object PaymentUrlValidator {
    fun isSafeToLaunch(url: String): Boolean {
        val trimmed = url.trim()
        if (trimmed.isEmpty()) return false
        val lower = trimmed.lowercase()
        return lower.startsWith("https://") || lower.startsWith("http://")
    }
}
