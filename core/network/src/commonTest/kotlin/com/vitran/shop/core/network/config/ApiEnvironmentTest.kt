package com.vitran.shop.core.network.config

import kotlin.test.Test
import kotlin.test.assertEquals

class ApiEnvironmentTest {

    @Test
    fun productionApiBaseUrlCombinesOriginAndVersionPath() {
        val env = ApiEnvironments.Production
        assertEquals("https://api.vitran.ir/api/v1", env.apiBaseUrl)
        assertEquals("https://api.vitran.ir/health", env.originUrl("/health"))
    }

    @Test
    fun localApiBaseUrlTrimsTrailingSlashFromOrigin() {
        val env = ApiEnvironment(origin = "http://localhost:8080/")
        assertEquals("http://localhost:8080/api/v1", env.apiBaseUrl)
    }
}
