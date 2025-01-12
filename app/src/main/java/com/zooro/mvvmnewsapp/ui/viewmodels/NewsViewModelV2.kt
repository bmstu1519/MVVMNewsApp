package com.zooro.mvvmnewsapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zooro.mvvmnewsapp.data.toDomain
import com.zooro.mvvmnewsapp.data.toNetwork
import com.zooro.mvvmnewsapp.domain.model.Article
import com.zooro.mvvmnewsapp.domain.model.NewsResponse
import com.zooro.mvvmnewsapp.domain.model.PaginationState
import com.zooro.mvvmnewsapp.domain.repository.NetworkHelperRepository
import com.zooro.mvvmnewsapp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NewsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isShowMenu: Boolean = false,
    val isDarkMode: Boolean = false,
    val breakingNews: NewsResponse? = null,
    val searchNews: NewsResponse? = null,
    val paginationState: PaginationState? = null
)

class NewsViewModelV2(
    private val newsRepository: NewsRepository,
    private val networkHelper: NetworkHelperRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NewsUiState())
    val state: StateFlow<NewsUiState> = _state.asStateFlow()

    private val searchQuery = MutableStateFlow("")

    companion object {
        private const val STARTING_PAGE = 1
        private const val PAGE_SIZE = 20
    }

    init {
        getBreakingNews("us")
    }

    fun handleToggleMenu() {
        updateState(isShowMenu = !_state.value.isShowMenu)
    }

    fun handleNightMode() {
        updateState(isDarkMode = !_state.value.isDarkMode)
    }

    fun getBreakingNews(countryCode: String, isFirstLoad: Boolean = false) = viewModelScope.launch {
        if (isFirstLoad) {
            resetPaginationState()
        }
        loadNextPage(
            loadPage = { page ->
                newsRepository.getBreakingNews(countryCode, page).map {
                    it.toDomain()
                }
            }
        )
    }

    fun searchNews(isFirstLoad: Boolean = false) = viewModelScope.launch {
        if (isFirstLoad) {
            resetPaginationState()
        } else {
            loadNextPage(
                loadPage = { page ->
                    newsRepository.searchNews(searchQuery.value, page).map {
                        it.toDomain()
                    }
                }
            )
        }
    }

    fun updateSearchQuery(query: String) {
        if (query.length >= 3) {
            searchQuery.value = query
            searchNews()
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

    private suspend fun loadNextPage(
        loadPage: suspend (Int) -> Result<NewsResponse>
    ) {
        val currentState = _state.value.paginationState

        if (shouldSkipLoading(currentState)) return

        updateState(
            paginationState = currentState?.copy(isLoading = true)
        )

        try {
            if (!networkHelper.isNetworkConnected()) {
                handleError("No internet connection", currentState)
                return
            }

            loadPage(currentState?.currentPage ?: STARTING_PAGE)
                .onSuccess { response ->
                    handleSuccessResponse(response, currentState)
                }
                .onFailure { exception ->
                    handleError(
                        exception.message ?: "Unknown error",
                        currentState
                    )
                }
        } catch (e: Exception) {
            handleError(
                e.localizedMessage ?: "Unknown error",
                currentState
            )
        }
    }

    private fun handleSuccessResponse(
        response: NewsResponse,
        currentState: PaginationState?
    ) {
        val newArticles = response.articles
        val currentItems = currentState?.items.orEmpty()
        val isFirstPage = currentState?.currentPage == STARTING_PAGE
        val updatedItems = if (isFirstPage) newArticles else currentItems + newArticles

        updateState(
            paginationState = PaginationState(
                isLoading = false,
                isLastPage = newArticles.isEmpty() || newArticles.size < PAGE_SIZE,
                currentPage = (currentState?.currentPage ?: STARTING_PAGE) + 1,
                items = updatedItems
            ),
            errorMessage = null
        )
    }

    private fun handleError(
        errorMessage: String,
        currentState: PaginationState?
    ) {
        updateState(
            paginationState = currentState?.copy(isLoading = false),
            errorMessage = errorMessage
        )
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.saveArticle(article.toNetwork())
    }

    fun getSavedNews(): Flow<List<Article>> = newsRepository.getSavedNews().map { article ->
        article.toDomain()
    }

    suspend fun isArticleSaved(url: String): Boolean = newsRepository.isArticleSaved(url) > 0

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article.toNetwork())
    }

    private fun updateState(
        isLoading: Boolean? = null,
        errorMessage: String? = null,
        isShowMenu: Boolean? = null,
        isDarkMode: Boolean? = null,
        breakingNews: NewsResponse? = null,
        searchNews: NewsResponse? = null,
        paginationState: PaginationState? = null
    ) {
        _state.update { currentState ->
            currentState.copy(
                isLoading = isLoading ?: currentState.isLoading,
                errorMessage = errorMessage ?: currentState.errorMessage,
                isShowMenu = isShowMenu ?: currentState.isShowMenu,
                isDarkMode = isDarkMode ?: currentState.isDarkMode,
                breakingNews = breakingNews ?: currentState.breakingNews,
                searchNews = searchNews ?: currentState.searchNews,
                paginationState = paginationState ?: currentState.paginationState
            )
        }
    }
}