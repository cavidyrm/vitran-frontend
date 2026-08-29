package com.vitran.shop.core.session.repository

/** Notified when credentials are cleared due to terminal auth failure or explicit logout. */
interface SessionInvalidationListener {
    suspend fun onSessionInvalidated()
}
