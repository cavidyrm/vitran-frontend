package com.vitran.shop.feature.engagement.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.core.domain.result.AppResult
import com.vitran.shop.core.session.domain.SessionState
import com.vitran.shop.core.session.repository.SessionRepository
import com.vitran.shop.feature.engagement.comment.domain.model.PublicShopComment
import com.vitran.shop.feature.engagement.comment.domain.model.SubmittedShopComment
import com.vitran.shop.feature.engagement.comment.domain.repository.ShopCommentRepository
import com.vitran.shop.feature.engagement.comment.domain.usecase.SubmitShopCommentUseCase
import com.vitran.shop.feature.marketplace.common.presentation.CursorListController
import com.vitran.shop.feature.marketplace.common.presentation.CursorListState
import com.vitran.shop.feature.marketplace.shop.domain.model.ShopId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ShopCommentsUiState(
    val list: CursorListState<PublicShopComment> = CursorListState(),
    val isSubmitting: Boolean = false,
    val pendingModeration: SubmittedShopComment? = null,
    val submitError: String? = null,
    val validationMessage: String? = null,
    val loginRequired: Boolean = false,
)

class ShopCommentsViewModel(
    private val shopId: ShopId,
    private val shopCommentRepository: ShopCommentRepository,
    private val submitShopComment: SubmitShopCommentUseCase,
    private val sessionRepository: SessionRepository,
) : ViewModel() {

    private val controller = CursorListController<PublicShopComment, Long>(idOf = { it.id.value })
    private val _uiState = MutableStateFlow(ShopCommentsUiState())
    val uiState: StateFlow<ShopCommentsUiState> = _uiState.asStateFlow()

    init {
        loadInitial()
    }

    fun retry() = loadInitial()

    fun loadNextPage() {
        val loadingMore = controller.beginLoadMore(_uiState.value.list) ?: return
        _uiState.value = _uiState.value.copy(list = loadingMore)
        viewModelScope.launch {
            loadPage(CursorPagination(cursor = _uiState.value.list.nextCursor), isInitial = false)
        }
    }

    fun submit(title: String, description: String) {
        if (sessionRepository.sessionState.value != SessionState.Authenticated) {
            _uiState.value = _uiState.value.copy(loginRequired = true)
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSubmitting = true,
                submitError = null,
                validationMessage = null,
                loginRequired = false,
            )
            when (val result = submitShopComment(shopId, title, description)) {
                is AppResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        pendingModeration = result.value,
                    )
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
            )
            loadPage(CursorPagination(), isInitial = true, generation = generation)
        }
    }

    private suspend fun loadPage(
        pagination: CursorPagination,
        isInitial: Boolean,
        generation: Int = controller.currentGeneration(),
    ) {
        when (val result = shopCommentRepository.getComments(shopId, pagination)) {
            is AppResult.Success -> {
                val list = _uiState.value.list
                val updated = if (isInitial || pagination.cursor == null) {
                    controller.applyInitialPage(list, result.value, generation)
                } else {
                    controller.applyLoadMorePage(list, result.value)
                }
                _uiState.value = _uiState.value.copy(list = updated)
            }
            is AppResult.Failure -> {
                val list = _uiState.value.list
                val updated = if (isInitial || pagination.cursor == null) {
                    controller.applyInitialFailure(list, result.error)
                } else {
                    controller.applyLoadMoreFailure(list, result.error)
                }
                _uiState.value = _uiState.value.copy(list = updated)
            }
        }
    }
}
