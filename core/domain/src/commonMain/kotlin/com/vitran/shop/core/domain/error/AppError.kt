package com.vitran.shop.core.domain.error

/**
 * Transport-independent application error hierarchy.
 * Feature repositories may refine generic variants into feature-specific errors.
 */
sealed interface AppError {
    val message: String?
    val httpStatus: Int?
    val backendCode: Int?
    val fieldErrors: List<FieldError>
    val errorDataJson: String?

    sealed interface Network : AppError {
        data class NoConnection(
            override val message: String? = null,
        ) : Network {
            override val httpStatus: Int? = null
            override val backendCode: Int? = null
            override val fieldErrors: List<FieldError> = emptyList()
            override val errorDataJson: String? = null
        }

        data class Timeout(
            override val message: String? = null,
        ) : Network {
            override val httpStatus: Int? = null
            override val backendCode: Int? = null
            override val fieldErrors: List<FieldError> = emptyList()
            override val errorDataJson: String? = null
        }

        data class ConnectionFailure(
            override val message: String? = null,
        ) : Network {
            override val httpStatus: Int? = null
            override val backendCode: Int? = null
            override val fieldErrors: List<FieldError> = emptyList()
            override val errorDataJson: String? = null
        }

        data class ServerUnavailable(
            override val message: String? = null,
            override val httpStatus: Int? = null,
            override val backendCode: Int? = null,
        ) : Network {
            override val fieldErrors: List<FieldError> = emptyList()
            override val errorDataJson: String? = null
        }
    }

    sealed interface Authentication : AppError {
        data class Unauthorized(
            override val message: String? = null,
            override val httpStatus: Int? = 401,
            override val backendCode: Int? = null,
            override val fieldErrors: List<FieldError> = emptyList(),
            override val errorDataJson: String? = null,
        ) : Authentication

        data class SessionExpired(
            override val message: String? = null,
            override val httpStatus: Int? = 401,
            override val backendCode: Int? = null,
            override val fieldErrors: List<FieldError> = emptyList(),
            override val errorDataJson: String? = null,
        ) : Authentication
    }

    data class Forbidden(
        override val message: String? = null,
        override val httpStatus: Int? = 403,
        override val backendCode: Int? = null,
        override val fieldErrors: List<FieldError> = emptyList(),
        override val errorDataJson: String? = null,
    ) : AppError

    data class NotFound(
        override val message: String? = null,
        override val httpStatus: Int? = 404,
        override val backendCode: Int? = null,
        override val fieldErrors: List<FieldError> = emptyList(),
        override val errorDataJson: String? = null,
    ) : AppError

    data class Conflict(
        override val message: String? = null,
        override val httpStatus: Int? = 409,
        override val backendCode: Int? = null,
        override val fieldErrors: List<FieldError> = emptyList(),
        override val errorDataJson: String? = null,
    ) : AppError

    data class Validation(
        override val message: String? = null,
        override val httpStatus: Int? = null,
        override val backendCode: Int? = null,
        override val fieldErrors: List<FieldError> = emptyList(),
        override val errorDataJson: String? = null,
    ) : AppError

    data class Server(
        override val message: String? = null,
        override val httpStatus: Int? = null,
        override val backendCode: Int? = null,
        override val fieldErrors: List<FieldError> = emptyList(),
        override val errorDataJson: String? = null,
    ) : AppError

    data class Serialization(
        override val message: String? = null,
    ) : AppError {
        override val httpStatus: Int? = null
        override val backendCode: Int? = null
        override val fieldErrors: List<FieldError> = emptyList()
        override val errorDataJson: String? = null
    }

    data class Unexpected(
        override val message: String? = null,
    ) : AppError {
        override val httpStatus: Int? = null
        override val backendCode: Int? = null
        override val fieldErrors: List<FieldError> = emptyList()
        override val errorDataJson: String? = null
    }
}
