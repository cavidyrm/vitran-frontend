package com.vitran.shop.feature.seller.shop.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.location.domain.model.CityId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopSlug
import com.vitran.shop.feature.seller.shop.domain.error.isSlugAlreadyTaken
import com.vitran.shop.feature.seller.shop.domain.model.CreateShopCommand
import com.vitran.shop.feature.seller.shop.domain.model.SellerShopDetails
import com.vitran.shop.feature.seller.shop.domain.repository.SellerShopRepository
import com.vitran.shop.feature.seller.shop.domain.usecase.CreateShopUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class SlugCheckUiStatus {
    data object Idle : SlugCheckUiStatus()
    data object Checking : SlugCheckUiStatus()
    data class Available(val slug: ShopSlug) : SlugCheckUiStatus()
    data class Taken(val slug: ShopSlug) : SlugCheckUiStatus()
    data class Error(val error: AppError) : SlugCheckUiStatus()
}

data class CreateShopUiState(
    val slugCheck: SlugCheckUiStatus = SlugCheckUiStatus.Idle,
    val isSubmitting: Boolean = false,
    val fieldErrors: Map<String, String> = emptyMap(),
    val generalError: AppError? = null,
    val createdShop: SellerShopDetails? = null,
)

sealed class CreateShopUiEffect {
    data class ShopCreated(val shopId: ShopId) : CreateShopUiEffect()
}

/**
 * Create-shop presentation. Form fields may live in the screen; this VM owns
 * slug-check, submit, and durable creation outcome.
 */
class CreateShopViewModel(
    private val createShopUseCase: CreateShopUseCase,
    private val sellerShopRepository: SellerShopRepository,
    private val slugDebounceMs: Long = 400L,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateShopUiState())
    val uiState: StateFlow<CreateShopUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<CreateShopUiEffect>(extraBufferCapacity = 1)
    val effects: SharedFlow<CreateShopUiEffect> = _effects.asSharedFlow()

    private var slugCheckJob: Job? = null
    private var submitJob: Job? = null

    fun onSlugInputChanged(rawSlug: String, excludeId: ShopId? = null) {
        slugCheckJob?.cancel()
        val trimmed = rawSlug.trim()
        if (trimmed.isEmpty() || !isLocallyValidSlug(trimmed)) {
            _uiState.update { it.copy(slugCheck = SlugCheckUiStatus.Idle) }
            return
        }
        slugCheckJob =
            viewModelScope.launch {
                _uiState.update { it.copy(slugCheck = SlugCheckUiStatus.Checking) }
                delay(slugDebounceMs)
                when (
                    val result =
                        sellerShopRepository.checkSlugAvailability(ShopSlug(trimmed), excludeId)
                ) {
                    is AppResult.Success -> {
                        val availability = result.value
                        _uiState.update {
                            it.copy(
                                slugCheck =
                                    if (availability.isAvailable) {
                                        SlugCheckUiStatus.Available(availability.slug)
                                    } else {
                                        SlugCheckUiStatus.Taken(availability.slug)
                                    },
                            )
                        }
                    }
                    is AppResult.Failure -> {
                        _uiState.update {
                            it.copy(slugCheck = SpugCheckError(result.error))
                        }
                    }
                }
            }
    }

    private fun SpugCheckError(error: AppError) = SlugCheckUiStatus.Error(error)

    fun submit(command: CreateShopCommand) {
        if (_uiState.value.isSubmitting) return
        val localErrors = mutableMapOf<String, String>()
        if (command.title.isBlank()) localErrors["title"] = "title"
        if (localErrors.isNotEmpty()) {
            _uiState.update { it.copy(fieldErrors = localErrors) }
            return
        }
        submitJob?.cancel()
        _uiState.update {
            it.copy(isSubmitting = true, generalError = null, fieldErrors = emptyMap())
        }
        submitJob =
            viewModelScope.launch {
                when (val result = createShopUseCase(command)) {
                    is AppResult.Success -> {
                        _uiState.update {
                            it.copy(isSubmitting = false, createdShop = result.value.shop)
                        }
                        _effects.emit(CreateShopUiEffect.ShopCreated(result.value.shop.id))
                    }
                    is AppResult.Failure -> {
                        val error = result.error
                        val fieldErrors =
                            error.fieldErrors.associate {
                                it.reason to (it.messages.firstOrNull() ?: it.reason)
                            }
                        val slugTaken = error.isSlugAlreadyTaken()
                        _uiState.update {
                            it.copy(
                                isSubmitting = false,
                                fieldErrors =
                                    if (slugTaken) {
                                        fieldErrors + ("slug" to (fieldErrors["slug"] ?: "slug"))
                                    } else {
                                        fieldErrors
                                    },
                                generalError = if (fieldErrors.isEmpty() && !slugTaken) error else null,
                                slugCheck =
                                    if (slugTaken && command.slug != null) {
                                        SlugCheckUiStatus.Taken(command.slug)
                                    } else {
                                        it.slugCheck
                                    },
                            )
                        }
                    }
                }
            }
    }

    fun clearCreatedOutcome() {
        _uiState.update { it.copy(createdShop = null) }
    }
}

/** Builds a create command from Create Store UI fields. Does not invent category IDs. */
fun buildCreateShopCommand(
    title: String,
    slug: String,
    omitSlug: Boolean,
    description: String,
    address: String,
    phoneNumber: String,
    cityId: CityId,
    whatsapp: String?,
    telegram: String?,
    instagram: String?,
    website: String?,
    type: String = "retailer",
): CreateShopCommand =
    CreateShopCommand(
        title = title.trim(),
        slug =
            if (omitSlug || slug.isBlank()) {
                null
            } else {
                ShopSlug(slug.trim())
            },
        description = description.takeIf { it.isNotBlank() },
        address = address.takeIf { it.isNotBlank() },
        phoneNumber = phoneNumber.takeIf { it.isNotBlank() },
        supportTimes = null,
        type = type.ifBlank { "retailer" },
        cityId = cityId,
        categoryNumericIds = emptyList(),
        whatsapp = whatsapp?.takeIf { it.isNotBlank() },
        telegram = telegram?.takeIf { it.isNotBlank() },
        instagram = instagram?.takeIf { it.isNotBlank() },
        website = website?.takeIf { it.isNotBlank() },
    )

internal fun isLocallyValidSlug(slug: String): Boolean =
    slug.length >= 2 && slug.all { it.isLetterOrDigit() || it == '-' || it == '_' }
