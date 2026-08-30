package com.vitran.shop.feature.engagement.session

/**
 * Stable visitor `session_id` for product contact and analytics.
 * Lifetime: current application process / user journey. Not persisted.
 * Not rotated on login. Never expose in UI.
 */
interface VisitorSessionProvider {
    fun sessionId(): String
}
