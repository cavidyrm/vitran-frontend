package com.vitran.shop.core.domain.auth

/**
 * Backend user roles from profile payloads.
 * Client-side checks are UX/navigation only — backend authorization is authoritative.
 */
sealed class UserRole {
    data object User : UserRole()
    data object Admin : UserRole()
    data object SuperAdmin : UserRole()
    data class Unknown(val rawValue: String) : UserRole()

    fun toBackend(): String = when (this) {
        User -> "user"
        Admin -> "admin"
        SuperAdmin -> "super_admin"
        is Unknown -> rawValue
    }

    companion object {
        fun fromBackend(value: String): UserRole = when (value.lowercase()) {
            "user", "customer", "seller" -> User
            "admin" -> Admin
            "super_admin" -> SuperAdmin
            else -> Unknown(value)
        }
    }
}
