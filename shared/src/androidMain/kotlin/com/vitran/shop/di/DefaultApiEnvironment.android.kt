package com.vitran.shop.di

import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.ApiEnvironments

actual fun defaultApiEnvironment(): ApiEnvironment = ApiEnvironments.Production
