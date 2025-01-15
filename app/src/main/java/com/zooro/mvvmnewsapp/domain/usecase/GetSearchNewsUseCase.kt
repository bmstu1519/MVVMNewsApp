package com.zooro.mvvmnewsapp.domain.usecase

import com.zooro.mvvmnewsapp.data.toDomain
import com.zooro.mvvmnewsapp.domain.model.NewsResponse
import com.zooro.mvvmnewsapp.domain.model.PaginationState
import com.zooro.mvvmnewsapp.domain.repository.NetworkHelperRepository
import com.zooro.mvvmnewsapp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update

data class SearchNewsState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val paginationState: PaginationState? = null
)

class GetSearchNewsUseCase(
    private val newsRepository: NewsRepository,
    private val networkHelper: NetworkHelperRepository
) {
    private val _state = MutableStateFlow(SearchNewsState())
    val state = _state.asStateFlow()

    companion object {
        private const val STARTING_PAGE = 1
        private const val PAGE_SIZE = 20
    }

    fun execute(
        searchQuery: String
    ): Flow<SearchNewsState> = flow {
//        resetPaginationState()

        val currentState = _state.value.paginationState
        if (shouldSkipLoading(currentState)) {
            emit(_state.value)
            return@flow
        }

        try {
            if (!networkHelper.isNetworkConnected()) {
                updateState(
                    errorMessage = "No internet connection",
                    paginationState = currentState?.copy(isLoading = false)
                )
                emit(_state.value)
                return@flow
            }

            updateState(
                isLoading = true,
                paginationState = currentState?.copy(isLoading = true)
            )
            emit(_state.value)

            if (searchQuery.length >= 3) {
                val result =  newsRepository.searchNews(
                    searchQuery,
                    currentState?.currentPage ?: STARTING_PAGE
                )

                result.fold(
                    onSuccess = { response ->
                        handleSuccessResponse(response.toDomain())
                    },
                    onFailure = { exception ->
                        handleError(exception.message ?: "Unknown error", currentState)
                    }
                )

                emit(_state.value)
            }
        } catch (e: Exception) {
            handleError(e.localizedMessage ?: "Unknown error", currentState)
            emit(_state.value)
        }
    }

    private fun resetPaginationState() {
        updateState(
            paginationState = PaginationState(
                currentPage = STARTING_PAGE,
                items = emptyList()
            )
        )
    }

    private fun shouldSkipLoading(currentState: PaginationState?): Boolean {
        return currentState?.isLastPage == true || currentState?.isLoading == true
    }

    private fun handleSuccessResponse(response: NewsResponse) {
        val currentState = _state.value.paginationState
        val newArticles = response.articles
        val currentItems = currentState?.items.orEmpty()
        val isFirstPage = currentState?.currentPage == STARTING_PAGE
        val updatedItems = if (isFirstPage) newArticles else currentItems + newArticles

        updateState(
            isLoading = false,
            errorMessage = null,
            paginationState = PaginationState(
                isLoading = false,
                isLastPage = newArticles.isEmpty() || newArticles.size < PAGE_SIZE,
                currentPage = (currentState?.currentPage ?: STARTING_PAGE) + 1,
                items = updatedItems
            )
        )
    }

    private fun handleError(errorMessage: String, currentState: PaginationState?) {
        updateState(
            isLoading = false,
            errorMessage = errorMessage,
            paginationState = currentState?.copy(isLoading = false)
        )
    }

    private fun updateState(
        isLoading: Boolean? = null,
        errorMessage: String? = null,
        paginationState: PaginationState? = null
    ) {
        _state.update { currentState ->
            currentState.copy(
                isLoading = isLoading ?: currentState.isLoading,
                errorMessage = errorMessage ?: currentState.errorMessage,
                paginationState = paginationState ?: currentState.paginationState
            )
        }
    }
}