package com.vitran.shop.feature.admin.state

import com.vitran.shop.core.session.repository.SessionInvalidationListener

class AdminSessionStateStore(
    invalidationListeners: MutableList<SessionInvalidationListener>,
) : SessionInvalidationListener {

    private val clearCallbacks = mutableListOf<() -> Unit>()

    init {
        invalidationListeners.add(this)
    }

    fun registerClearCallback(callback: () -> Unit): () -> Unit {
        clearCallbacks.add(callback)
        return { clearCallbacks.remove(callback) }
    }

    fun clear() {
        clearCallbacks.forEach { it() }
    }

    override suspend fun onSessionInvalidated() {
        clear()
    }
}
