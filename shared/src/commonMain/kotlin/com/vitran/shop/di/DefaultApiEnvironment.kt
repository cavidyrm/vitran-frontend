package com.vitran.shop.di

import com.vitran.shop.core.network.config.ApiEnvironment

/**
 * Native targets use the dedicated API host. Browser uses the page origin so
 * `/api` and `/health` stay same-origin (Traefik / webpack proxy) and avoid CORS.
 */
expect fun defaultApiEnvironment(): ApiEnvironment
