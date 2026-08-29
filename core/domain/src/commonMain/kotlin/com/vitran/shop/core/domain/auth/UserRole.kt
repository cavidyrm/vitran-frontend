package com.vitran.shop.core.domain.auth

/**
 * Backend user roles from JWT / profile payloads.
 * Client-side checks are UX/navigation only — backend authorization is authoritative.
 */
enum class UserRole {
    Customer,
    Seller,
    Admin,
    SuperAdmin,
}
