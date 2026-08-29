package com.vitran.shop.core.session

import com.vitran.shop.core.domain.auth.UserRole

/**
 * Phase 2 stub — always unauthenticated until Phase 3 secure session storage.
 */
class EmptySessionReader : SessionReader {
    override val isAuthenticated: Boolean = false
    override val roles: Set<UserRole> = emptySet()
    override fun accessTokenOrNull(): String? = null
}
