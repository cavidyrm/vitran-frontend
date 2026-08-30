package com.vitran.shop.feature.admin.catalog.taxonomy.data.remote

import com.vitran.shop.core.domain.auth.AuthMode
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.network.config.ApiEnvironment
import com.vitran.shop.core.network.config.apiUrl
import com.vitran.shop.core.network.executor.ApiRequestExecutor
import com.vitran.shop.core.network.request.authMode
import com.vitran.shop.core.platform.file.SelectedFile
import com.vitran.shop.core.platform.file.safeFileName
import com.vitran.shop.feature.admin.catalog.taxonomy.data.remote.dto.TaxonomyNameRequestDto
import com.vitran.shop.feature.taxonomy.domain.model.AttributeSlug
import com.vitran.shop.feature.taxonomy.domain.model.AttributeValueSlug
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.FormBuilder
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

internal class AdminTaxonomyApi(
    private val client: HttpClient,
    private val environment: ApiEnvironment,
    private val executor: ApiRequestExecutor,
) {
    suspend fun importTaxonomy(
        categories: SelectedFile,
        attributes: SelectedFile,
    ): AppResult<Unit> {
        val body = buildTaxonomyImportMultipart(categories, attributes)
        return executor.executeEmpty {
            client.post(environment.apiUrl("/admin/taxonomy/import")) {
                authMode(AuthMode.Required)
                setBody(body)
            }
        }
    }

    suspend fun renameCategory(slug: CategorySlug, name: String): AppResult<Unit> =
        patchName("/admin/categories/${slug.value}/name", name)

    suspend fun uploadCategoryIcon(slug: CategorySlug, image: SelectedFile): AppResult<Unit> {
        val body = buildCategoryIconMultipart(image)
        return executor.executeEmpty {
            client.put(environment.apiUrl("/admin/categories/${slug.value}/icon")) {
                authMode(AuthMode.Required)
                setBody(body)
            }
        }
    }

    suspend fun renameAttribute(slug: AttributeSlug, name: String): AppResult<Unit> =
        patchName("/admin/attributes/${slug.value}/name", name)

    suspend fun renameValue(slug: AttributeValueSlug, name: String): AppResult<Unit> =
        patchName("/admin/values/${slug.value}/name", name)

    private suspend fun patchName(path: String, name: String): AppResult<Unit> =
        executor.executeEmpty {
            client.patch(environment.apiUrl(path)) {
                authMode(AuthMode.Required)
                contentType(ContentType.Application.Json)
                setBody(TaxonomyNameRequestDto(name))
            }
        }
}

internal suspend fun buildTaxonomyImportMultipart(
    categories: SelectedFile,
    attributes: SelectedFile,
): MultiPartFormDataContent {
    val categoryPart = categories.preparePart()
    val attributePart = attributes.preparePart()
    return MultiPartFormDataContent(
        formData {
            appendFile("categories", categoryPart)
            appendFile("attributes", attributePart)
        },
    )
}

internal suspend fun buildCategoryIconMultipart(image: SelectedFile): MultiPartFormDataContent {
    val part = image.preparePart()
    return MultiPartFormDataContent(formData { appendFile("image", part) })
}

private data class PreparedFilePart(
    val fileName: String,
    val contentType: ContentType,
    val bytes: ByteArray,
)

private suspend fun SelectedFile.preparePart() =
    PreparedFilePart(
        fileName = safeFileName(name),
        contentType = contentType?.let { ContentType.parse(it) } ?: ContentType.Application.OctetStream,
        bytes = readBytes(),
    )

private fun FormBuilder.appendFile(key: String, part: PreparedFilePart) {
    append(
        key = key,
        value = part.bytes,
        headers =
            Headers.build {
                append(HttpHeaders.ContentType, part.contentType.toString())
                append(HttpHeaders.ContentDisposition, "filename=\"${part.fileName}\"")
            },
    )
}
