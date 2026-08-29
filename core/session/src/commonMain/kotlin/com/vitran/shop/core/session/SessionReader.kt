package com.vitran.shop.core.session

import com.vitran.shop.core.domain.auth.UserRole

/**
 * Read-only session contract for cross-feature access (Home, Seller, Admin).
 * Implementation and secure storage arrive in Phase 3.
 */
interface SessionReader {
    val isAuthenticated: Boolean
    val roles: Set<UserRole>
    fun accessTokenOrNull(): String?
}
