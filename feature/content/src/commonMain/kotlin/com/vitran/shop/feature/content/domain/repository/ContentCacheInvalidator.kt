package com.vitran.shop.feature.content.domain.repository

fun interface ContentCacheInvalidator {
    suspend fun invalidate()
}
