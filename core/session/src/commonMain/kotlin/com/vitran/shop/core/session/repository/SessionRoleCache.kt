package com.vitran.shop.core.session.repository

import com.vitran.shop.core.domain.auth.UserRole

class SessionRoleCache {
    private val _roles = mutableSetOf<UserRole>()

    val roles: Set<UserRole> get() = _roles.toSet()

    fun update(roles: Set<UserRole>) {
        _roles.clear()
        _roles.addAll(roles)
    }

    fun clear() {
        _roles.clear()
    }
}
