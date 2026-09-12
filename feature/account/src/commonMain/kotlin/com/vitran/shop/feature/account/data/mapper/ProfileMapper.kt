package com.vitran.shop.feature.account.data.mapper

import com.vitran.shop.feature.account.data.remote.dto.PersonDto
import com.vitran.shop.feature.account.data.remote.dto.PersonWriteRequestDto
import com.vitran.shop.feature.account.data.remote.dto.ProfileSlotDto
import com.vitran.shop.feature.account.data.remote.dto.SizeSlotAssignmentDto
import com.vitran.shop.feature.account.data.remote.dto.SizingProfileDto
import com.vitran.shop.feature.account.data.remote.dto.UpdateSizingRequestDto
import com.vitran.shop.feature.account.domain.model.CreatePersonCommand
import com.vitran.shop.feature.account.domain.model.Person
import com.vitran.shop.feature.account.domain.model.PersonId
import com.vitran.shop.feature.account.domain.model.PersonRelation
import com.vitran.shop.feature.account.domain.model.PersonSex
import com.vitran.shop.feature.account.domain.model.ProfileSlot
import com.vitran.shop.feature.account.domain.model.SizeSlotValue
import com.vitran.shop.feature.account.domain.model.SizingProfile
import com.vitran.shop.feature.account.domain.model.UpdatePersonCommand
import com.vitran.shop.feature.account.domain.model.UpdateSizingCommand

internal fun SizingProfileDto.toDomain(): SizingProfile =
    SizingProfile(
        productMatchNotify = productMatchNotify,
        persons = persons.map(PersonDto::toDomain),
        slots = slots.map(ProfileSlotDto::toDomain),
    )

internal fun ProfileSlotDto.toDomain(): ProfileSlot =
    ProfileSlot(
        slot = slot,
        label = label.orEmpty(),
        defaultAttributeSlug = defaultAttributeSlug,
        sortOrder = sortOrder,
    )

internal fun PersonDto.toDomain(): Person =
    Person(
        id = PersonId(id),
        name = name,
        relation = relation.toPersonRelation(),
        sex = sex.toPersonSex(),
        notify = notify,
        sortOrder = sortOrder,
        sizes = sizes.mapValues { (_, assignment) -> assignment.toDomain() },
    )

internal fun SizeSlotAssignmentDto.toDomain(): SizeSlotValue =
    SizeSlotValue(valueSlug = valueSlug, attributeSlug = attributeSlug)

internal fun SizeSlotValue.toDto(): SizeSlotAssignmentDto =
    SizeSlotAssignmentDto(valueSlug = valueSlug, attributeSlug = attributeSlug)

internal fun Map<String, SizeSlotValue>.toDto(): Map<String, SizeSlotAssignmentDto> =
    mapValues { (_, value) -> value.toDto() }

internal fun UpdateSizingCommand.toRequestDto(): UpdateSizingRequestDto =
    UpdateSizingRequestDto(
        name = name,
        relation = "self",
        sex = sex?.toApiValue(),
        notify = notify,
        sizes = sizes.takeIf { it.isNotEmpty() }?.toDto(),
    )

internal fun CreatePersonCommand.toRequestDto(): PersonWriteRequestDto =
    PersonWriteRequestDto(
        name = name,
        relation = relation.toApiValue(),
        sex = sex?.toApiValue(),
        notify = notify,
        sortOrder = sortOrder,
        sizes = sizes.takeIf { it.isNotEmpty() }?.toDto(),
    )

internal fun UpdatePersonCommand.toRequestDto(): PersonWriteRequestDto =
    PersonWriteRequestDto(
        name = name,
        sex = sex?.toApiValue(),
        notify = notify,
        sortOrder = sortOrder,
        sizes = sizes?.toDto(),
    )

internal fun String?.toPersonRelation(): PersonRelation =
    when (this?.lowercase()) {
        "self" -> PersonRelation.Self
        "partner" -> PersonRelation.Partner
        "child" -> PersonRelation.Child
        "other" -> PersonRelation.Other
        null, "" -> PersonRelation.Unknown("")
        else -> PersonRelation.Unknown(this)
    }

internal fun PersonRelation.toApiValue(): String =
    when (this) {
        PersonRelation.Self -> "self"
        PersonRelation.Partner -> "partner"
        PersonRelation.Child -> "child"
        PersonRelation.Other -> "other"
        is PersonRelation.Unknown -> raw
    }

internal fun String?.toPersonSex(): PersonSex? =
    when (this?.lowercase()) {
        null, "" -> null
        "male" -> PersonSex.Male
        "female" -> PersonSex.Female
        else -> PersonSex.Unknown(this)
    }

internal fun PersonSex.toApiValue(): String =
    when (this) {
        PersonSex.Male -> "male"
        PersonSex.Female -> "female"
        is PersonSex.Unknown -> raw
    }
