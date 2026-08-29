package com.vitran.shop.core.session.time

import kotlinx.datetime.Instant
import kotlin.time.Clock as TimeClock

interface AppClock {
    fun now(): Instant
}

class SystemAppClock : AppClock {
    override fun now(): Instant =
        Instant.fromEpochMilliseconds(TimeClock.System.now().toEpochMilliseconds())
}

class FakeAppClock(
    private var current: Instant,
) : AppClock {
    override fun now(): Instant = current

    fun setInstant(instant: Instant) {
        current = instant
    }

    fun advanceSeconds(seconds: Long) {
        current = Instant.fromEpochSeconds(current.epochSeconds + seconds)
    }
}
