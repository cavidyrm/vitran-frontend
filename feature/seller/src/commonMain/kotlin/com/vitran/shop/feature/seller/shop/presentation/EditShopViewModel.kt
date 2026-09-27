package com.vitran.shop.feature.seller.shop.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.location.domain.model.CityId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopSlug
import com.vitran.shop.feature.seller.shop.domain.error.isSlugAlreadyTaken
import com.vitran.shop.feature.seller.shop.domain.model.SellerShopDetails
import com.vitran.shop.feature.seller.shop.domain.model.UpdateShopCommand
import com.vitran.shop.feature.seller.shop.domain.repository.SellerShopRepository
import com.vitran.shop.feature.seller.shop.domain.usecase.UpdateShopUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditShopFormState(
    val title: String = "",
    val slug: String = "",
    val description: String = "",
    val address: String = "",
    val phoneNumber: String = "",
    val supportTimes: String = "",
    val type: String = "retailer",
    val cityId: CityId? = null,
    val whatsapp: String = "",
    val telegram: String = "",
    val instagram: String = "",
    val website: String = "",
    val avatarUrl: String = "",
    val categorySlugs: List<String> = emptyList(),
)

data class EditShopUiState(
    val isLoading: Boolean = true,
    val form: EditShopFormState = EditShopFormState(),
    val loadedShop: SellerShopDetails? = null,
    val slugCheck: SlugCheckUiStatus = SlugCheckUiStatus.Idle,
    val isSubmitting: Boolean = false,
    val fieldErrors: Map<String, String> = emptyMap(),
    val generalError: AppError? = null,
    val loadError: AppError? = null,
    val savedShop: SellerShopDetails? = null,
)

/** Deferred UI — edit form populated from seller GET, not public shop API. */
class EditShopViewModel(
    private val shopId: ShopId,
    private val sellerShopRepository: SellerShopRepository,
    private val updateShopUseCase: UpdateShopUseCase,
    private val slugDebounceMs: Long = 400L,
) : ViewModel() {
    private val _uiState = MutableStateFlow(EditShopUiState())
    val uiState: StateFlow<EditShopUiState> = _uiState.asStateFlow()

    private var slugCheckJob: Job? = null
    private var submitJob: Job? = null
    private var originalSlug: String? = null
    private var originalCategorySlugs: List<String> = emptyList()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loadError = null) }
            when (val result = sellerShopRepository.getMyShop(shopId)) {
                is AppResult.Success -> {
                    val shop = result.value
                    val form = shop.toEditForm()
                    originalSlug = shop.slug.value
                    originalCategorySlugs = form.categorySlugs
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loadedShop = shop,
                            form = form,
                            slugCheck = SlugCheckUiStatus.Idle,
                        )
                    }
                }
                is AppResult.Failure -> {
                    _uiState.update {
                        it.copy(isLoading = false, loadError = result.error)
                    }
                }
            }
        }
    }

    fun updateForm(transform: (EditShopFormState) -> EditShopFormState) {
        _uiState.update {
            it.copy(
                form = transform(it.form),
                fieldErrors = emptyMap(),
                generalError = null,
                savedShop = null,
            )
        }
    }

    fun onSlugChanged(raw: String) {
        val slug = raw.trim()
        _uiState.update {
            it.copy(form = it.form.copy(slug = slug), savedShop = null, fieldErrors = emptyMap())
        }
        if (slug == originalSlug) {
            slugCheckJob?.cancel()
            _uiState.update { it.copy(slugCheck = SlugCheckUiStatus.Idle) }
            return
        }
        slugCheckJob?.cancel()
        if (slug.isEmpty() || !isLocallyValidSlug(slug)) {
            _uiState.update { it.copy(slugCheck = SlugCheckUiStatus.Idle) }
            return
        }
        slugCheckJob =
            viewModelScope.launch {
                _uiState.update { it.copy(slugCheck = SlugCheckUiStatus.Checking) }
                delay(slugDebounceMs)
                when (
                    val result =
                        sellerShopRepository.checkSlugAvailability(ShopSlug(slug), excludeId = shopId)
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
                            it.copy(slugCheck = SlugCheckUiStatus.Error(result.error))
                        }
                    }
                }
            }
    }

    fun toggleCategory(slug: String) {
        updateForm { form ->
            val next =
                if (slug in form.categorySlugs) {
                    form.categorySlugs.filterNot { it == slug }
                } else {
                    form.categorySlugs + slug
                }
            form.copy(categorySlugs = next)
        }
    }

    fun save() {
        if (_uiState.value.isSubmitting) return
        if (_uiState.value.slugCheck is SlugCheckUiStatus.Taken) return
        val form = _uiState.value.form
        val categoriesChanged = form.categorySlugs.toSet() != originalCategorySlugs.toSet()
        val command =
            UpdateShopCommand(
                shopId = shopId,
                title = form.title.takeIf { it.isNotBlank() },
                slug = form.slug.takeIf { it.isNotBlank() }?.let { ShopSlug(it) },
                avatarUrl = form.avatarUrl.trim().takeIf { it.isNotEmpty() },
                description = form.description,
                address = form.address,
                phoneNumber = form.phoneNumber,
                supportTimes = form.supportTimes,
                type = form.type.takeIf { it.isNotBlank() },
                cityId = form.cityId,
                categorySlugs = form.categorySlugs.takeIf { categoriesChanged },
                whatsapp = form.whatsapp,
                telegram = form.telegram,
                instagram = form.instagram,
                website = form.website,
            )
        submitJob?.cancel()
        submitJob =
            viewModelScope.launch {
                _uiState.update {
                    it.copy(isSubmitting = true, generalError = null, fieldErrors = emptyMap())
                }
                when (val result = updateShopUseCase(command)) {
                    is AppResult.Success -> {
                        val returned = result.value
                        val slugs =
                            if (!categoriesChanged && returned.categorySlugs.isEmpty()) {
                                originalCategorySlugs
                            } else {
                                returned.categorySlugs
                            }
                        val shop = returned.copy(categorySlugs = slugs)
                        val form = shop.toEditForm()
                        _uiState.update {
                            it.copy(
                                isSubmitting = false,
                                savedShop = shop,
                                loadedShop = shop,
                                form = form,
                            )
                        }
                        originalSlug = shop.slug.value
                        originalCategorySlugs = form.categorySlugs
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
                            )
                        }
                    }
                }
            }
    }
}

private fun SellerShopDetails.toEditForm(): EditShopFormState =
    EditShopFormState(
        title = title.orEmpty(),
        slug = slug.value,
        description = description.orEmpty(),
        address = address.orEmpty(),
        phoneNumber = phoneNumber.orEmpty(),
        supportTimes = supportTimes.orEmpty(),
        type = type ?: "retailer",
        cityId = cityId,
        whatsapp = whatsapp.orEmpty(),
        telegram = telegram.orEmpty(),
        instagram = instagram.orEmpty(),
        website = website.orEmpty(),
        avatarUrl = avatarUrl.orEmpty(),
        categorySlugs = categorySlugs.map { it.trim() }.filter { it.isNotEmpty() },
    )
