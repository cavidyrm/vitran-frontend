package com.vitran.shop.core.platform.crash

/**
 * Vendor-neutral crash / non-fatal reporting.
 * Production binds [NoOpCrashReporter] until an EXTERNAL provider is configured.
 * Never attach phone, email, tokens, API keys, payment authority, or comment bodies.
 */
interface CrashReporter {
    fun recordException(throwable: Throwable, breadcrumbs: Map<String, String> = emptyMap())
    fun recordNonFatal(message: String, breadcrumbs: Map<String, String> = emptyMap())
    fun setAppContext(keys: Map<String, String>)
    fun clearUserContext()
}

class NoOpCrashReporter : CrashReporter {
    override fun recordException(throwable: Throwable, breadcrumbs: Map<String, String>) = Unit
    override fun recordNonFatal(message: String, breadcrumbs: Map<String, String>) = Unit
    override fun setAppContext(keys: Map<String, String>) = Unit
    override fun clearUserContext() = Unit
}
