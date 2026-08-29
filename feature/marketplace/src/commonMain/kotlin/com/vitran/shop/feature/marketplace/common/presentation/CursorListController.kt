package com.vitran.shop.feature.marketplace.common.presentation

import com.vitran.shop.core.domain.error.AppError
import com.vitran.shop.core.domain.pagination.CursorPage
import com.vitran.shop.core.domain.pagination.CursorPagination
import com.vitran.shop.core.domain.result.AppResult

/**
 * Shared cursor-list presentation state for marketplace list/search screens.
 */
data class CursorListState<T>(
    val items: List<T> = emptyList(),
    val nextCursor: String? = null,
    val hasMore: Boolean = false,
    val isLoadingInitial: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val initialError: AppError? = null,
    val paginationError: AppError? = null,
    val refreshError: AppError? = null,
) {
    val hasContent: Boolean get() = items.isNotEmpty()
}

class CursorListController<T, Id>(
    private val idOf: (T) -> Id,
    private var requestGeneration: Int = 0,
) {
    fun beginInitialLoad(state: CursorListState<T>): CursorListState<T> =
        state.copy(
            isLoadingInitial = true,
            initialError = null,
            paginationError = null,
            refreshError = null,
        )

    fun beginRefresh(state: CursorListState<T>): CursorListState<T> {
        requestGeneration += 1
        return state.copy(
            isRefreshing = true,
            refreshError = null,
            paginationError = null,
        )
    }

    fun beginLoadMore(state: CursorListState<T>): CursorListState<T>? =
        if (state.isLoadingMore || !state.hasMore || state.isLoadingInitial) {
            null
        } else {
            state.copy(isLoadingMore = true, paginationError = null)
        }

    fun applyInitialPage(
        state: CursorListState<T>,
        page: CursorPage<T>,
        generation: Int,
    ): CursorListState<T> {
        if (generation != requestGeneration) return state
        return state.copy(
            items = page.items,
            nextCursor = page.nextCursor,
            hasMore = page.hasMore,
            isLoadingInitial = false,
            isRefreshing = false,
            initialError = null,
        )
    }

    fun applyRefreshPage(
        state: CursorListState<T>,
        page: CursorPage<T>,
        generation: Int,
    ): CursorListState<T> {
        if (generation != requestGeneration) return state
        return state.copy(
            items = page.items,
            nextCursor = page.nextCursor,
            hasMore = page.hasMore,
            isRefreshing = false,
            refreshError = null,
            paginationError = null,
        )
    }

    fun applyLoadMorePage(
        state: CursorListState<T>,
        page: CursorPage<T>,
    ): CursorListState<T> {
        val merged = (state.items + page.items).distinctBy { idOf(it) }
        return state.copy(
            items = merged,
            nextCursor = page.nextCursor,
            hasMore = page.hasMore,
            isLoadingMore = false,
            paginationError = null,
        )
    }

    fun applyInitialFailure(state: CursorListState<T>, error: AppError): CursorListState<T> =
        state.copy(isLoadingInitial = false, initialError = error)

    fun applyRefreshFailure(state: CursorListState<T>, error: AppError): CursorListState<T> =
        state.copy(isRefreshing = false, refreshError = error)

    fun applyLoadMoreFailure(state: CursorListState<T>, error: AppError): CursorListState<T> =
        state.copy(isLoadingMore = false, paginationError = error)

    fun resetForNewQuery(): Int {
        requestGeneration += 1
        return requestGeneration
    }

    fun currentGeneration(): Int = requestGeneration

    fun nextPagination(state: CursorListState<T>): CursorPagination? =
        if (state.hasMore) {
            CursorPagination(cursor = state.nextCursor)
        } else {
            null
        }
}

inline fun <T> handleCursorResult(
    result: AppResult<CursorPage<T>>,
    onSuccess: (CursorPage<T>) -> Unit,
    onFailure: (AppError) -> Unit,
) {
    when (result) {
        is AppResult.Success -> onSuccess(result.value)
        is AppResult.Failure -> onFailure(result.error)
    }
}
