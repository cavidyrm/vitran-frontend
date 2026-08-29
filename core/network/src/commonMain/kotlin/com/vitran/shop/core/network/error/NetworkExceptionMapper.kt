package com.vitran.shop.core.network.error

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.network.client.LocalAuthException
import com.vitran.shop.core.network.logging.NetworkLogger
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

internal object NetworkExceptionMapper {

    fun map(throwable: Throwable, logger: NetworkLogger): AppError =
        when (throwable) {
            is HttpRequestTimeoutException,
            is ConnectTimeoutException,
            is SocketTimeoutException,
            -> AppError.Network.Timeout(message = throwable.message)

            is IOException -> AppError.Network.ConnectionFailure(message = throwable.message)

            is SerializationException -> AppError.Serialization(message = throwable.message)

            is LocalAuthException -> throwable.error

            else -> {
                logger.error("Unexpected network failure", throwable)
                AppError.Unexpected(message = throwable.message)
            }
        }
}
