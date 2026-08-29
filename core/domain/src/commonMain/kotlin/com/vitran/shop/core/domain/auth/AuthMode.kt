package com.vitran.shop.core.domain.auth

/**
 * How an API endpoint expects authentication.
 * Phase 2 network layer uses this when attaching Bearer tokens.
 */
enum class AuthMode {
    /** No Bearer token required. */
    None,

    /** Works anonymously; authenticated callers may receive richer data. */
    Optional,

    /** Bearer access token required. */
    Required,
}
