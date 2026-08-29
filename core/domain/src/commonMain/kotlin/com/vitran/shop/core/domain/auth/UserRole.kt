package com.vitran.shop.core.domain.auth

/**
 * Backend user roles from profile payloads.
 * Client-side checks are UX/navigation only — backend authorization is authoritative.
 */
sealed class UserRole {
    data object Customer : UserRole()
    data object Seller : UserRole()
    data object Admin : UserRole()
    data object SuperAdmin : UserRole()
    data class Unknown(val rawValue: String) : UserRole()

    companion object {
        fun fromBackend(value: String): UserRole = when (value.lowercase()) {
            "customer" -> Customer
            "seller" -> Seller
            "admin" -> Admin
            "super_admin" -> SuperAdmin
            else -> Unknown(value)
        }
    }
}
