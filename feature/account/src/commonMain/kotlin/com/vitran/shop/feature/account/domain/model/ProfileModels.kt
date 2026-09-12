package com.vitran.shop.feature.account.domain.model

import kotlin.jvm.JvmInline

@JvmInline
value class PersonId(val value: Long)

data class SizeSlotValue(
    val valueSlug: String,
    val attributeSlug: String? = null,
)

sealed interface PersonRelation {
    data object Self : PersonRelation
    data object Partner : PersonRelation
    data object Child : PersonRelation
    data object Other : PersonRelation
    data class Unknown(val raw: String) : PersonRelation
}

sealed interface PersonSex {
    data object Male : PersonSex
    data object Female : PersonSex
    data class Unknown(val raw: String) : PersonSex
}

data class ProfileSlot(
    val slot: String,
    val label: String,
    val defaultAttributeSlug: String?,
    val sortOrder: Int,
)

data class Person(
    val id: PersonId,
    val name: String?,
    val relation: PersonRelation,
    val sex: PersonSex?,
    val notify: Boolean,
    val sortOrder: Int,
    val sizes: Map<String, SizeSlotValue>,
)

data class SizingProfile(
    val productMatchNotify: Boolean,
    val persons: List<Person>,
    val slots: List<ProfileSlot>,
)

data class ProductMatchNotifySettings(
    val productMatchNotify: Boolean,
)

data class UpdateSizingCommand(
    val name: String? = null,
    val sex: PersonSex? = null,
    val notify: Boolean? = null,
    val sizes: Map<String, SizeSlotValue> = emptyMap(),
)

data class CreatePersonCommand(
    val name: String,
    val relation: PersonRelation,
    val sex: PersonSex? = null,
    val notify: Boolean? = null,
    val sortOrder: Int? = null,
    val sizes: Map<String, SizeSlotValue> = emptyMap(),
)

data class UpdatePersonCommand(
    val id: PersonId,
    val name: String? = null,
    val sex: PersonSex? = null,
    val notify: Boolean? = null,
    val sortOrder: Int? = null,
    val sizes: Map<String, SizeSlotValue>? = null,
)
