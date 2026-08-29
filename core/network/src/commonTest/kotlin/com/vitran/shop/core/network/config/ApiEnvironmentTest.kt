package com.vitran.shop.core.network.config

import kotlin.test.Test
import kotlin.test.assertEquals

class ApiEnvironmentTest {

    @Test
    fun productionApiBaseUrlCombinesOriginAndVersionPath() {
        val env = ApiEnvironment(origin = "https://vitran.ir")
        assertEquals("https://vitran.ir/api/v1", env.apiBaseUrl)
    }

    @Test
    fun localApiBaseUrlTrimsTrailingSlashFromOrigin() {
        val env = ApiEnvironment(origin = "http://localhost:8080/")
        assertEquals("http://localhost:8080/api/v1", env.apiBaseUrl)
    }
}
