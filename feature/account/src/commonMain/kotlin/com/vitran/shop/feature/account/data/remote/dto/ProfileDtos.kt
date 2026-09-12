package com.vitran.shop.feature.account.data.remote.dto

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

/** Slot assignment: string (`"size__l"`) or `{ attribute_slug, value_slug }`. */
@Serializable(with = SizeSlotAssignmentDtoSerializer::class)
internal data class SizeSlotAssignmentDto(
    val valueSlug: String,
    val attributeSlug: String? = null,
)

internal object SizeSlotAssignmentDtoSerializer : KSerializer<SizeSlotAssignmentDto> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("SizeSlotAssignment") {
            element<String>("value_slug")
            element<String>("attribute_slug", isOptional = true)
        }

    override fun serialize(encoder: Encoder, value: SizeSlotAssignmentDto) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: error("SizeSlotAssignmentDtoSerializer requires JSON")
        if (value.attributeSlug == null) {
            jsonEncoder.encodeJsonElement(JsonPrimitive(value.valueSlug))
        } else {
            jsonEncoder.encodeJsonElement(
                buildJsonObject {
                    put("attribute_slug", value.attributeSlug)
                    put("value_slug", value.valueSlug)
                },
            )
        }
    }

    override fun deserialize(decoder: Decoder): SizeSlotAssignmentDto {
        val jsonDecoder = decoder as? JsonDecoder
            ?: error("SizeSlotAssignmentDtoSerializer requires JSON")
        return when (val element = jsonDecoder.decodeJsonElement()) {
            is JsonPrimitive -> SizeSlotAssignmentDto(valueSlug = element.content)
            is JsonObject -> {
                val obj = element.jsonObject
                SizeSlotAssignmentDto(
                    valueSlug = obj["value_slug"]?.jsonPrimitive?.contentOrNull.orEmpty(),
                    attributeSlug = obj["attribute_slug"]?.jsonPrimitive?.contentOrNull,
                )
            }
            else -> SizeSlotAssignmentDto(valueSlug = "")
        }
    }
}

@Serializable
internal data class SizingProfileDataDto(
    val profile: SizingProfileDto,
)

@Serializable
internal data class SizingProfileDto(
    @SerialName("product_match_notify") val productMatchNotify: Boolean = true,
    val persons: List<PersonDto> = emptyList(),
    val slots: List<ProfileSlotDto> = emptyList(),
)

@Serializable
internal data class ProfileSlotDto(
    val slot: String,
    val label: String? = null,
    @SerialName("default_attribute_slug") val defaultAttributeSlug: String? = null,
    @SerialName("sort_order") val sortOrder: Int = 0,
)

@Serializable
internal data class PersonDto(
    val id: Long,
    val name: String? = null,
    val relation: String? = null,
    val sex: String? = null,
    val notify: Boolean = true,
    @SerialName("sort_order") val sortOrder: Int = 0,
    val sizes: Map<String, SizeSlotAssignmentDto> = emptyMap(),
)

@Serializable
internal data class PersonsDataDto(
    val persons: List<PersonDto> = emptyList(),
)

@Serializable
internal data class PersonDataDto(
    val person: PersonDto,
)

@Serializable
internal data class UpdateSizingRequestDto(
    val name: String? = null,
    val relation: String = "self",
    val sex: String? = null,
    val notify: Boolean? = null,
    val sizes: Map<String, SizeSlotAssignmentDto>? = null,
)

@Serializable
internal data class PersonWriteRequestDto(
    val name: String? = null,
    val relation: String? = null,
    val sex: String? = null,
    val notify: Boolean? = null,
    @SerialName("sort_order") val sortOrder: Int? = null,
    val sizes: Map<String, SizeSlotAssignmentDto>? = null,
)

@Serializable
internal data class NotifySettingsDataDto(
    val notify: NotifySettingsDto,
)

@Serializable
internal data class NotifySettingsDto(
    @SerialName("product_match_notify") val productMatchNotify: Boolean = true,
)

@Serializable
internal data class UpdateNotifyRequestDto(
    @SerialName("product_match_notify") val productMatchNotify: Boolean,
)
