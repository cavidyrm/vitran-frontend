package com.vitran.shop.core.session.time

import kotlin.time.Duration.Companion.seconds

/** Treat tokens expiring within this window as stale and refresh proactively. */
object TokenExpirationPolicy {
    val expirationSkew = 30.seconds
}
