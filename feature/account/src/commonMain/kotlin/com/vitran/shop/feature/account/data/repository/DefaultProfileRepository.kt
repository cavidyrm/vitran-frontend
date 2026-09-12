package com.vitran.shop.feature.account.data.repository

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.account.data.mapper.toDomain
import com.vitran.shop.feature.account.data.mapper.toRequestDto
import com.vitran.shop.feature.account.data.remote.ProfileApi
import com.vitran.shop.feature.account.data.remote.dto.UpdateNotifyRequestDto
import com.vitran.shop.feature.account.domain.model.CreatePersonCommand
import com.vitran.shop.feature.account.domain.model.Person
import com.vitran.shop.feature.account.domain.model.PersonId
import com.vitran.shop.feature.account.domain.model.ProductMatchNotifySettings
import com.vitran.shop.feature.account.domain.model.SizingProfile
import com.vitran.shop.feature.account.domain.model.UpdatePersonCommand
import com.vitran.shop.feature.account.domain.model.UpdateSizingCommand
import com.vitran.shop.feature.account.domain.repository.ProfileRepository

internal class DefaultProfileRepository(
    private val api: ProfileApi,
) : ProfileRepository {
    override suspend fun getSizingProfile(): AppResult<SizingProfile> =
        api.getSizingProfile().mapSuccess { it.profile.toDomain() }

    override suspend fun updateSizingProfile(command: UpdateSizingCommand): AppResult<SizingProfile> =
        api.updateSizingProfile(command.toRequestDto()).mapSuccess { it.profile.toDomain() }

    override suspend fun getNotifySettings(): AppResult<ProductMatchNotifySettings> =
        api.getNotifySettings().mapSuccess {
            ProductMatchNotifySettings(productMatchNotify = it.notify.productMatchNotify)
        }

    override suspend fun updateNotifySettings(
        productMatchNotify: Boolean,
    ): AppResult<ProductMatchNotifySettings> =
        api.updateNotifySettings(UpdateNotifyRequestDto(productMatchNotify)).mapSuccess {
            ProductMatchNotifySettings(productMatchNotify = it.notify.productMatchNotify)
        }

    override suspend fun listPersons(): AppResult<List<Person>> =
        api.listPersons().mapSuccess { it.persons.map { person -> person.toDomain() } }

    override suspend fun createPerson(command: CreatePersonCommand): AppResult<Person> =
        api.createPerson(command.toRequestDto()).mapSuccess { it.person.toDomain() }

    override suspend fun updatePerson(command: UpdatePersonCommand): AppResult<Person> =
        api.updatePerson(command.id, command.toRequestDto()).mapSuccess { it.person.toDomain() }

    override suspend fun deletePerson(id: PersonId): AppResult<Unit> =
        api.deletePerson(id)
}

private inline fun <T, R> AppResult<T>.mapSuccess(transform: (T) -> R): AppResult<R> = when (this) {
    is AppResult.Success -> AppResult.Success(transform(value))
    is AppResult.Failure -> this
}
