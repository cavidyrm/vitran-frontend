package com.vitran.shop.feature.engagement.session

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class DefaultVisitorSessionProvider(
    initialSessionId: String? = null,
) : VisitorSessionProvider {

    private val id: String = initialSessionId ?: Uuid.random().toString()

    override fun sessionId(): String = id
}
