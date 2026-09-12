package com.vitran.shop.feature.account.domain.repository

import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.account.domain.model.CreatePersonCommand
import com.vitran.shop.feature.account.domain.model.Person
import com.vitran.shop.feature.account.domain.model.PersonId
import com.vitran.shop.feature.account.domain.model.ProductMatchNotifySettings
import com.vitran.shop.feature.account.domain.model.SizingProfile
import com.vitran.shop.feature.account.domain.model.UpdatePersonCommand
import com.vitran.shop.feature.account.domain.model.UpdateSizingCommand

interface ProfileRepository {
    suspend fun getSizingProfile(): AppResult<SizingProfile>

    suspend fun updateSizingProfile(command: UpdateSizingCommand): AppResult<SizingProfile>

    suspend fun getNotifySettings(): AppResult<ProductMatchNotifySettings>

    suspend fun updateNotifySettings(productMatchNotify: Boolean): AppResult<ProductMatchNotifySettings>

    suspend fun listPersons(): AppResult<List<Person>>

    suspend fun createPerson(command: CreatePersonCommand): AppResult<Person>

    suspend fun updatePerson(command: UpdatePersonCommand): AppResult<Person>

    suspend fun deletePerson(id: PersonId): AppResult<Unit>
}
