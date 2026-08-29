package com.vitran.shop.core.session

/**
 * Token types defined by the backend auth contract.
 * Secure storage and refresh belong to Phase 3 — not Room or plain preferences.
 */
enum class TokenKind {
    Access,
    Refresh,
    Temp,
}
