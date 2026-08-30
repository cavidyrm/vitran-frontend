package com.vitran.shop.feature.engagement.comment.data.remote

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.apiUrl
import com.vitran.shop.core.network.executor.ApiRequestExecutor
import com.vitran.shop.core.network.pagination.appendCursorPagination
import com.vitran.shop.core.network.request.authMode
import com.vitran.shop.feature.engagement.comment.data.remote.dto.CommentsDataDto
import com.vitran.shop.feature.engagement.comment.data.remote.dto.CreateShopCommentRequestDto
import com.vitran.shop.feature.engagement.comment.data.remote.dto.SubmittedCommentDataDto
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class ShopCommentApi(
    private val client: HttpClient,
    private val environment: ApiEnvironment,
    private val executor: ApiRequestExecutor,
) {
    suspend fun getComments(
        shopId: ShopId,
        pagination: CursorPagination,
    ): AppResult<CommentsDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/shops/${shopId.value}/comments")) {
                authMode(AuthMode.None)
                url { parameters.appendCursorPagination(pagination) }
            }
        }

    suspend fun createComment(
        shopId: ShopId,
        request: CreateShopCommentRequestDto,
    ): AppResult<SubmittedCommentDataDto> =
        executor.execute {
            client.post(environment.apiUrl("/shops/${shopId.value}/comments")) {
                authMode(AuthMode.Required)
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }
}
