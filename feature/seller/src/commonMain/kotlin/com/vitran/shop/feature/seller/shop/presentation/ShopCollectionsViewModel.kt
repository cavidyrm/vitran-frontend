package com.vitran.shop.feature.seller.shop.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopCollection
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopCollectionId
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import com.vitran.shop.feature.marketplace.shop.domain.query.ShopCollectionListQuery
import com.vitran.shop.feature.seller.shop.domain.model.CreateShopCollectionCommand
import com.vitran.shop.feature.seller.shop.domain.model.UpdateShopCollectionCommand
import com.vitran.shop.feature.seller.shop.domain.repository.SellerShopRepository
import com.vitran.shop.feature.seller.shop.domain.usecase.CreateShopCollectionUseCase
import com.vitran.shop.feature.seller.shop.domain.usecase.DeleteShopCollectionUseCase
import com.vitran.shop.feature.seller.shop.domain.usecase.UpdateShopCollectionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ShopCollectionEditorState(
    val collectionId: ShopCollectionId? = null,
    val title: String = "",
    val description: String = "",
    val productIdsText: String = "",
    val productIdsEdited: Boolean = false,
)

data class ShopCollectionsUiState(
    val isLoading: Boolean = true,
    val items: List<ShopCollection> = emptyList(),
    val error: String? = null,
    val editor: ShopCollectionEditorState? = null,
    val isSaving: Boolean = false,
    val message: String? = null,
)

class ShopCollectionsViewModel(
    private val shopId: ShopId,
    private val sellerShopRepository: SellerShopRepository,
    private val createShopCollection: CreateShopCollectionUseCase,
    private val updateShopCollection: UpdateShopCollectionUseCase,
    private val deleteShopCollection: DeleteShopCollectionUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShopCollectionsUiState())
    val uiState: StateFlow<ShopCollectionsUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (
                val result =
                    sellerShopRepository.getMyCollections(
                        ShopCollectionListQuery(shopId = shopId, pagination = CursorPagination()),
                    )
            ) {
                is AppResult.Success ->
                    _uiState.update {
                        it.copy(isLoading = false, items = result.value.items, error = null)
                    }
                is AppResult.Failure ->
                    _uiState.update {
                        it.copy(isLoading = false, error = result.error.message ?: "خطا در بارگذاری کالکشن‌ها")
                    }
            }
        }
    }

    fun startCreate() {
        _uiState.update {
            it.copy(editor = ShopCollectionEditorState(), message = null, error = null)
        }
    }

    fun startEdit(collection: ShopCollection) {
        _uiState.update {
            it.copy(
                editor =
                    ShopCollectionEditorState(
                        collectionId = collection.id,
                        title = collection.title,
                        description = collection.description.orEmpty(),
                    ),
                message = null,
                error = null,
            )
        }
    }

    fun updateEditor(transform: (ShopCollectionEditorState) -> ShopCollectionEditorState) {
        _uiState.update { state ->
            val editor = state.editor ?: return@update state
            state.copy(editor = transform(editor), error = null)
        }
    }

    fun cancelEditor() {
        _uiState.update { it.copy(editor = null) }
    }

    fun saveEditor() {
        val editor = _uiState.value.editor ?: return
        if (_uiState.value.isSaving) return
        val replaceProducts = editor.collectionId == null || editor.productIdsEdited
        val productIds =
            if (replaceProducts) {
                parseProductIds(editor.productIdsText)
            } else {
                null
            }
        if (replaceProducts && productIds == null) {
            _uiState.update { it.copy(error = "شناسه محصول باید عدد باشد") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null, message = null) }
            val existingId = editor.collectionId
            val result =
                if (existingId == null) {
                    createShopCollection(
                        CreateShopCollectionCommand(
                            shopId = shopId,
                            title = editor.title,
                            description = editor.description.trim().takeIf { it.isNotEmpty() },
                            productIds = productIds.orEmpty(),
                        ),
                    )
                } else {
                    updateShopCollection(
                        UpdateShopCollectionCommand(
                            shopId = shopId,
                            collectionId = existingId,
                            title = editor.title,
                            description = editor.description,
                            productIds = productIds,
                        ),
                    )
                }
            when (result) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isSaving = false, editor = null, message = "کالکشن ذخیره شد") }
                    load()
                }
                is AppResult.Failure ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            error = result.error.message ?: "ذخیره کالکشن ناموفق بود",
                        )
                    }
            }
        }
    }

    fun delete(collectionId: ShopCollectionId) {
        if (_uiState.value.isSaving) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null, message = null) }
            when (val result = deleteShopCollection(shopId, collectionId)) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isSaving = false, message = "کالکشن حذف شد") }
                    load()
                }
                is AppResult.Failure ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            error = result.error.message ?: "حذف کالکشن ناموفق بود",
                        )
                    }
            }
        }
    }
}

internal fun parseProductIds(raw: String): List<ProductId>? {
    val tokens = raw.split(',', '،', '\n', ' ').map { it.trim() }.filter { it.isNotEmpty() }
    if (tokens.isEmpty()) return emptyList()
    val ids = tokens.map { token -> token.toLongOrNull() ?: return null }
    return ids.map(::ProductId)
}
