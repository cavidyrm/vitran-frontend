package com.vitran.shop.feature.seller.product.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.error.FieldError
import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.platform.file.ImagePicker
import com.vitran.shop.core.platform.file.SelectedFile
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.seller.product.domain.model.CreateProductCommand
import com.vitran.shop.feature.seller.product.domain.model.ProductPublicationState
import com.vitran.shop.feature.seller.product.domain.model.SellerProductDetails
import com.vitran.shop.feature.seller.product.domain.usecase.CreateProductUseCase
import com.vitran.shop.feature.seller.shop.domain.model.SellerShopSummary
import com.vitran.shop.feature.seller.shop.domain.query.SellerShopFilter
import com.vitran.shop.feature.seller.shop.domain.query.SellerShopListQuery
import com.vitran.shop.feature.seller.shop.domain.repository.SellerShopRepository
import com.vitran.shop.feature.seller.subscription.domain.usecase.GetShopEntitlementsUseCase
import com.vitran.shop.feature.taxonomy.domain.model.CategorySlug
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Fallback when shop entitlements are not yet loaded. */
private const val DefaultMaxProductImages = 5

enum class CreateProductSubmitMode {
    Draft,
    Publish,
}

data class LocalProductImage(
    val id: String,
    val fileName: String,
    val previewBytes: ByteArray?,
)

data class CreateProductFieldErrors(
    val title: String? = null,
    val price: String? = null,
    val category: String? = null,
    val summary: String? = null,
) {
    val hasAny: Boolean
        get() = title != null || price != null || category != null || summary != null
}

data class CreateProductUiState(
    val shopsLoading: Boolean = true,
    val shops: List<SellerShopSummary> = emptyList(),
    val selectedShopId: ShopId? = null,
    val storeName: String = "",
    val shopsError: AppError? = null,
    val maxImages: Int = DefaultMaxProductImages,
    val maxProducts: Int? = null,
    val isPickingImages: Boolean = false,
    val isSubmitting: Boolean = false,
    val fieldErrors: CreateProductFieldErrors = CreateProductFieldErrors(),
    val generalError: AppError? = null,
    val createdProduct: SellerProductDetails? = null,
    val localFilesByMediaId: Map<String, SelectedFile> = emptyMap(),
)

sealed class CreateProductUiEffect {
    data class ProductCreated(
        val productId: Long,
        val publicationState: ProductPublicationState,
    ) : CreateProductUiEffect()
}

@OptIn(ExperimentalUuidApi::class)
class CreateProductViewModel(
    private val createProductUseCase: CreateProductUseCase,
    private val sellerShopRepository: SellerShopRepository,
    private val imagePicker: ImagePicker,
    private val getShopEntitlements: GetShopEntitlementsUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateProductUiState())
    val uiState: StateFlow<CreateProductUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<CreateProductUiEffect>(extraBufferCapacity = 1)
    val effects: SharedFlow<CreateProductUiEffect> = _effects.asSharedFlow()

    private var submitJob: Job? = null
    private var pickJob: Job? = null

    init {
        loadShops()
    }

    fun loadShops() {
        viewModelScope.launch {
            _uiState.update { it.copy(shopsLoading = true, shopsError = null) }
            when (
                val result =
                    sellerShopRepository.getMyShops(
                        SellerShopListQuery(
                            activeFilter = SellerShopFilter.All,
                            pagination = CursorPagination(perPage = 50),
                        ),
                    )
            ) {
                is AppResult.Success -> {
                    val shops = result.value.items
                    val selected = shops.firstOrNull()
                    _uiState.update {
                        it.copy(
                            shopsLoading = false,
                            shops = shops,
                            selectedShopId = selected?.id,
                            storeName = selected?.title.orEmpty(),
                            shopsError =
                                if (shops.isEmpty()) {
                                    AppError.Validation(message = "فروشگاهی برای افزودن محصول یافت نشد")
                                } else {
                                    null
                                },
                        )
                    }
                    selected?.id?.let { refreshEntitlements(it) }
                }
                is AppResult.Failure -> {
                    _uiState.update {
                        it.copy(shopsLoading = false, shopsError = result.error)
                    }
                }
            }
        }
    }

    fun selectShop(shopId: ShopId) {
        val shop = _uiState.value.shops.firstOrNull { it.id == shopId } ?: return
        _uiState.update { it.copy(selectedShopId = shopId, storeName = shop.title) }
        refreshEntitlements(shopId)
    }

    private fun refreshEntitlements(shopId: ShopId) {
        viewModelScope.launch {
            when (val result = getShopEntitlements(shopId)) {
                is AppResult.Success -> {
                    val e = result.value
                    _uiState.update {
                        it.copy(
                            maxImages = e.limits.maxImages.takeIf { n -> n > 0 } ?: DefaultMaxProductImages,
                            maxProducts = e.limits.maxProducts,
                        )
                    }
                }
                is AppResult.Failure -> {
                    // Keep defaults; server remains authoritative on submit.
                }
            }
        }
    }

    fun pickImages(
        currentMediaCount: Int,
        onResult: (List<LocalProductImage>) -> Unit,
    ) {
        if (_uiState.value.isPickingImages || _uiState.value.isSubmitting) return
        val maxImages = _uiState.value.maxImages
        val remaining = maxImages - currentMediaCount
        if (remaining <= 0) return
        pickJob?.cancel()
        pickJob =
            viewModelScope.launch {
                _uiState.update { it.copy(isPickingImages = true) }
                val files = imagePicker.pickImages(remaining)
                if (files.isEmpty()) {
                    _uiState.update { it.copy(isPickingImages = false) }
                    onResult(emptyList())
                    return@launch
                }
                val items = mutableListOf<LocalProductImage>()
                val fileMap = _uiState.value.localFilesByMediaId.toMutableMap()
                for (file in files) {
                    val id = "local-${Uuid.random()}"
                    val bytes = runCatching { file.readBytes() }.getOrNull()
                    fileMap[id] = file
                    items +=
                        LocalProductImage(
                            id = id,
                            fileName = file.name,
                            previewBytes = bytes,
                        )
                }
                _uiState.update {
                    it.copy(isPickingImages = false, localFilesByMediaId = fileMap)
                }
                onResult(items)
            }
    }

    fun removeLocalImage(mediaId: String) {
        _uiState.update {
            it.copy(localFilesByMediaId = it.localFilesByMediaId - mediaId)
        }
    }

    fun submit(
        title: String,
        description: String,
        priceText: String,
        categoryId: String?,
        orderedMediaIds: List<String>,
        mode: CreateProductSubmitMode,
    ) {
        if (_uiState.value.isSubmitting) return
        val shopId = _uiState.value.selectedShopId
        if (shopId == null) {
            _uiState.update {
                it.copy(generalError = AppError.Validation(message = "فروشگاه انتخاب نشده است"))
            }
            return
        }
        val category = categoryId?.takeIf { it.isNotBlank() }?.let { CategorySlug(it) }
        if (category == null) {
            _uiState.update {
                it.copy(fieldErrors = CreateProductFieldErrors(category = "دسته‌بندی الزامی است"))
            }
            return
        }
        val priceAmount = priceText.trim().toLongOrNull()
        if (priceAmount == null || priceAmount < 0) {
            _uiState.update {
                it.copy(fieldErrors = CreateProductFieldErrors(price = "قیمت نامعتبر است"))
            }
            return
        }
        val trimmedTitle = title.trim()
        if (trimmedTitle.isBlank()) {
            _uiState.update {
                it.copy(fieldErrors = CreateProductFieldErrors(title = "عنوان الزامی است"))
            }
            return
        }

        val images =
            orderedMediaIds.mapNotNull { id ->
                _uiState.value.localFilesByMediaId[id]
            }

        val command =
            CreateProductCommand(
                shopId = shopId,
                title = trimmedTitle,
                description = description,
                priceAmount = priceAmount,
                category = category,
                desiredActive = mode == CreateProductSubmitMode.Publish,
                images = images,
            )

        val maxImages = _uiState.value.maxImages
        submitJob?.cancel()
        submitJob =
            viewModelScope.launch {
                _uiState.update {
                    it.copy(
                        isSubmitting = true,
                        fieldErrors = CreateProductFieldErrors(),
                        generalError = null,
                    )
                }
                when (val result = createProductUseCase(command, maxImages = maxImages)) {
                    is AppResult.Success -> {
                        val details = result.value
                        _uiState.update {
                            it.copy(
                                isSubmitting = false,
                                createdProduct = details,
                                localFilesByMediaId = emptyMap(),
                            )
                        }
                        _effects.emit(
                            CreateProductUiEffect.ProductCreated(
                                productId = details.id.value,
                                publicationState = details.publicationState,
                            ),
                        )
                    }
                    is AppResult.Failure -> {
                        _uiState.update {
                            it.copy(
                                isSubmitting = false,
                                fieldErrors = mapFieldErrors(result.error.fieldErrors),
                                generalError = result.error,
                            )
                        }
                    }
                }
            }
    }
}

private fun mapFieldErrors(errors: List<FieldError>): CreateProductFieldErrors {
    fun messageFor(keys: Set<String>): String? =
        errors
            .firstOrNull { it.reason.lowercase() in keys }
            ?.messages
            ?.firstOrNull()

    return CreateProductFieldErrors(
        title = messageFor(setOf("title")),
        price = messageFor(setOf("price")),
        category = messageFor(setOf("category_slug", "category")),
        summary = messageFor(setOf("images", "description", "active")),
    )
}
