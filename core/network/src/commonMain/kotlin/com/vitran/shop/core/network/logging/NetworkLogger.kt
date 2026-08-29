package com.vitran.shop.core.network.logging

interface NetworkLogger {
    fun debug(message: String)
    fun warn(message: String)
    fun error(message: String, throwable: Throwable? = null)
}

object NoOpNetworkLogger : NetworkLogger {
    override fun debug(message: String) = Unit
    override fun warn(message: String) = Unit
    override fun error(message: String, throwable: Throwable?) = Unit
}
