package com.vitran.shop.feature.engagement.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.session.domain.SessionState
import com.vitran.shop.core.session.repository.SessionRepository
import com.vitran.shop.feature.engagement.contact.domain.model.PurchaseIntentId
import com.vitran.shop.feature.engagement.review.domain.model.ProductReview
import com.vitran.shop.feature.engagement.review.domain.repository.ProductReviewRepository
import com.vitran.shop.feature.engagement.review.domain.usecase.SubmitProductReviewUseCase
import com.vitran.shop.feature.marketplace.common.presentation.CursorListController
import com.vitran.shop.feature.marketplace.common.presentation.CursorListState
import com.vitran.shop.feature.marketplace.product.domain.model.ProductId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProductReviewsUiState(
    val list: CursorListState<ProductReview> = CursorListState(),
    val isSubmitting: Boolean = false,
    val submitError: String? = null,
    val fieldErrors: List<AppError> = emptyList(),
    val submitted: Boolean = false,
    val validationMessage: String? = null,
)

class ProductReviewsViewModel(
    private val productId: ProductId,
    private val productReviewRepository: ProductReviewRepository,
    private val submitProductReview: SubmitProductReviewUseCase,
    private val sessionRepository: SessionRepository,
    private val purchaseIntentId: PurchaseIntentId? = null,
) : ViewModel() {

    private val controller = CursorListController<ProductReview, Long>(idOf = { it.id.value })
    private val _uiState = MutableStateFlow(ProductReviewsUiState())
    val uiState: StateFlow<ProductReviewsUiState> = _uiState.asStateFlow()

    init {
        loadInitial()
    }

    fun retry() = loadInitial()

    fun loadNextPage() {
        val loadingMore = controller.beginLoadMore(_uiState.value.list) ?: return
        _uiState.value = _uiState.value.copy(list = loadingMore)
        viewModelScope.launch {
            loadPage(
                pagination = CursorPagination(cursor = _uiState.value.list.nextCursor),
                isRefresh = false,
                isInitial = false,
            )
        }
    }

    fun submit(ratingValue: Int, comment: String) {
        if (sessionRepository.sessionState.value != SessionState.Authenticated) {
            _uiState.value = _uiState.value.copy(validationMessage = "login_required")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSubmitting = true,
                submitError = null,
                validationMessage = null,
                submitted = false,
            )
            when (
                val result = submitProductReview(
                    productId = productId,
                    ratingValue = ratingValue,
                    comment = comment,
                    intentId = purchaseIntentId,
                )
            ) {
                is AppResult.Success -> {
                    _uiState.value = _uiState.value.copy(isSubmitting = false, submitted = true)
                    loadInitial()
                }
                is AppResult.Failure -> {
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        submitError = result.error.message,
                        validationMessage = if (result.error is AppError.Validation) {
                            result.error.message
                        } else {
                            null
                        },
                    )
                }
            }
        }
    }

    private fun loadInitial() {
        viewModelScope.launch {
            val generation = controller.resetForNewQuery()
            _uiState.value = _uiState.value.copy(
                list = controller.beginInitialLoad(CursorListState()),
                submitted = false,
            )
            loadPage(CursorPagination(), isRefresh = false, isInitial = true, generation = generation)
        }
    }

    private suspend fun loadPage(
        pagination: CursorPagination,
        isRefresh: Boolean,
        isInitial: Boolean,
        generation: Int = controller.currentGeneration(),
    ) {
        when (val result = productReviewRepository.getReviews(productId, pagination)) {
            is AppResult.Success -> {
                val list = _uiState.value.list
                val updated = when {
                    isRefresh -> controller.applyRefreshPage(list, result.value, generation)
                    isInitial || pagination.cursor == null ->
                        controller.applyInitialPage(list, result.value, generation)
                    else -> controller.applyLoadMorePage(list, result.value)
                }
                _uiState.value = _uiState.value.copy(list = updated)
            }
            is AppResult.Failure -> {
                val list = _uiState.value.list
                val updated = when {
                    isRefresh -> controller.applyRefreshFailure(list, result.error)
                    isInitial || pagination.cursor == null ->
                        controller.applyInitialFailure(list, result.error)
                    else -> controller.applyLoadMoreFailure(list, result.error)
                }
                _uiState.value = _uiState.value.copy(list = updated)
            }
        }
    }
}
