package com.vitran.shop.feature.engagement.review.data.remote

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.apiUrl
import com.vitran.shop.core.network.executor.ApiRequestExecutor
import com.vitran.shop.core.network.pagination.appendCursorPagination
import com.vitran.shop.core.network.request.authMode
import com.vitran.shop.feature.engagement.review.data.remote.dto.CreateReviewRequestDto
import com.vitran.shop.feature.engagement.review.data.remote.dto.ReviewsDataDto
import com.vitran.shop.feature.engagement.review.data.remote.dto.SubmittedReviewDataDto
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class ProductReviewApi(
    private val client: HttpClient,
    private val environment: ApiEnvironment,
    private val executor: ApiRequestExecutor,
) {
    suspend fun getReviews(
        productId: ProductId,
        pagination: CursorPagination,
    ): AppResult<ReviewsDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/products/${productId.value}/reviews")) {
                authMode(AuthMode.None)
                url { parameters.appendCursorPagination(pagination) }
            }
        }

    suspend fun createReview(
        productId: ProductId,
        request: CreateReviewRequestDto,
    ): AppResult<SubmittedReviewDataDto> =
        executor.execute {
            client.post(environment.apiUrl("/products/${productId.value}/reviews")) {
                authMode(AuthMode.Required)
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }
}
