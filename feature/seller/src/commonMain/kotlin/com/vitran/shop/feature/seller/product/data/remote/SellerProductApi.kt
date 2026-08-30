package com.vitran.shop.feature.seller.product.data.remote

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.apiUrl
import com.vitran.shop.core.network.executor.ApiRequestExecutor
import com.vitran.shop.core.network.pagination.appendCursorPagination
import com.vitran.shop.core.network.request.authMode
import com.vitran.shop.core.platform.file.SelectedFile
import com.vitran.shop.core.platform.file.safeFileName
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.seller.product.data.remote.dto.SellerProductDataDto
import com.vitran.shop.feature.seller.product.data.remote.dto.SellerProductsDataDto
import com.vitran.shop.feature.seller.product.data.remote.dto.SetProductActiveRequestDto
import com.vitran.shop.feature.seller.product.domain.model.CreateProductCommand
import com.vitran.shop.feature.seller.product.domain.model.ProductImageId
import com.vitran.shop.feature.seller.product.domain.model.UpdateProductCommand
import com.vitran.shop.feature.seller.product.domain.query.SellerProductActiveFilter
import com.vitran.shop.feature.seller.product.domain.query.SellerProductListQuery
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.forms.FormBuilder
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

internal class SellerProductApi(
    private val client: HttpClient,
    private val environment: ApiEnvironment,
    private val executor: ApiRequestExecutor,
) {
    suspend fun listMyProducts(query: SellerProductListQuery): AppResult<SellerProductsDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/seller/products")) {
                authMode(AuthMode.Required)
                url {
                    parameters.appendCursorPagination(query.pagination)
                    query.shopId?.let { parameters.append("shop_id", it.value.toString()) }
                    when (query.activeFilter) {
                        SellerProductActiveFilter.All -> Unit
                        SellerProductActiveFilter.Active -> parameters.append("active", "true")
                        SellerProductActiveFilter.Inactive -> parameters.append("active", "false")
                    }
                    query.confirmed?.let { parameters.append("confirmed", it.toString()) }
                    query.categorySlug?.let { parameters.append("category_slug", it.value) }
                }
            }
        }

    suspend fun getMyProduct(productId: ProductId): AppResult<SellerProductDataDto> =
        executor.execute {
            client.get(environment.apiUrl("/seller/products/${productId.value}")) {
                authMode(AuthMode.Required)
            }
        }

    suspend fun createProduct(command: CreateProductCommand): AppResult<SellerProductDataDto> {
        val body = buildCreateMultipart(command)
        return executor.execute {
            client.post(environment.apiUrl("/seller/shops/${command.shopId.value}/products")) {
                authMode(AuthMode.Required)
                setBody(body)
            }
        }
    }

    suspend fun updateProduct(command: UpdateProductCommand): AppResult<SellerProductDataDto> {
        val body = buildUpdateMultipart(command)
        return executor.execute {
            client.patch(environment.apiUrl("/seller/products/${command.productId.value}")) {
                authMode(AuthMode.Required)
                setBody(body)
            }
        }
    }

    suspend fun setProductActive(
        productId: ProductId,
        active: Boolean,
    ): AppResult<SellerProductDataDto> =
        executor.execute {
            client.patch(environment.apiUrl("/seller/products/${productId.value}/active")) {
                authMode(AuthMode.Required)
                contentType(ContentType.Application.Json)
                setBody(SetProductActiveRequestDto(active = active))
            }
        }

    suspend fun deleteProduct(productId: ProductId): AppResult<Unit> =
        executor.executeEmpty {
            client.delete(environment.apiUrl("/seller/products/${productId.value}")) {
                authMode(AuthMode.Required)
            }
        }

    suspend fun deleteProductImage(
        productId: ProductId,
        imageId: ProductImageId,
    ): AppResult<SellerProductDataDto> =
        executor.execute {
            client.delete(
                environment.apiUrl("/seller/products/${productId.value}/images/${imageId.value}"),
            ) {
                authMode(AuthMode.Required)
            }
        }
}

internal suspend fun buildCreateMultipart(command: CreateProductCommand): MultiPartFormDataContent {
    val imageParts = prepareImageParts(command.images)
    return MultiPartFormDataContent(
        formData {
            append("title", command.title)
            append("category_slug", command.category.value)
            append("price", command.priceAmount.toString())
            append("description", command.description)
            append("active", command.desiredActive.toString())
            appendPreparedImages(imageParts)
        },
    )
}

internal suspend fun buildUpdateMultipart(command: UpdateProductCommand): MultiPartFormDataContent {
    val imageParts = prepareImageParts(command.images)
    return MultiPartFormDataContent(
        formData {
            command.title?.let { append("title", it) }
            command.category?.let { append("category_slug", it.value) }
            command.priceAmount?.let { append("price", it.toString()) }
            command.description?.let { append("description", it) }
            command.desiredActive?.let { append("active", it.toString()) }
            appendPreparedImages(imageParts)
        },
    )
}

private data class PreparedImagePart(
    val fileName: String,
    val contentType: ContentType,
    val bytes: ByteArray,
)

private suspend fun prepareImageParts(images: List<SelectedFile>): List<PreparedImagePart> =
    images.map { file ->
        PreparedImagePart(
            fileName = safeFileName(file.name),
            contentType = file.contentType?.let { ContentType.parse(it) } ?: ContentType.Application.OctetStream,
            bytes = file.readBytes(),
        )
    }

private fun FormBuilder.appendPreparedImages(parts: List<PreparedImagePart>) {
    for (part in parts) {
        append(
            key = "images",
            value = part.bytes,
            headers =
                Headers.build {
                    append(HttpHeaders.ContentType, part.contentType.toString())
                    append(HttpHeaders.ContentDisposition, "filename=\"${part.fileName}\"")
                },
        )
    }
}
