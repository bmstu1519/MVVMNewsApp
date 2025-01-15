package com.zooro.mvvmnewsapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zooro.mvvmnewsapp.domain.model.Article
import com.zooro.mvvmnewsapp.domain.model.NewsResponse
import com.zooro.mvvmnewsapp.domain.model.PaginationState
import com.zooro.mvvmnewsapp.domain.usecase.GetArticleUseCase
import com.zooro.mvvmnewsapp.domain.usecase.GetBreakingNewsUseCase
import com.zooro.mvvmnewsapp.domain.usecase.GetSavedNewsUseCase
import com.zooro.mvvmnewsapp.domain.usecase.GetSearchNewsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NewsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isShowMenu: Boolean = false,
    val isDarkMode: Boolean = false,
    val breakingNews: NewsResponse? = null,
    val paginationState: PaginationState? = null,
    val isArticleSaved: Boolean? = null
)

class NewsViewModel(
    private val getBreakingNewsUseCase: GetBreakingNewsUseCase,
    private val getSearchNewsUseCase: GetSearchNewsUseCase,
    private val getArticleUseCase: GetArticleUseCase,
    private val getSavedNewsUseCase: GetSavedNewsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(NewsUiState())
    val state: StateFlow<NewsUiState> = _state.asStateFlow()

    init {
        observeBreakingNews()
        observeSearchingNews()
        observeArticle()
        observeSavedNews()
    }

    fun getBreakingNews(countryCode: String, isFirstLoad: Boolean = false) = viewModelScope.launch {
        getBreakingNewsUseCase.execute(countryCode, isFirstLoad)
            .collect()
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        getSearchNewsUseCase.execute(searchQuery)
            .collect()
    }

    fun checkArticleSaved(article: Article) = viewModelScope.launch {
        getArticleUseCase.checkArticleSaved(article).collect()
    }

    fun getSavedNews() = viewModelScope.launch {
        getSavedNewsUseCase().collect()
    }

    fun deleteArticle(article: Article) = viewModelScope.launch {
        getArticleUseCase.deleteArticle(article)
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        getArticleUseCase.saveArticle(article)
    }
//////////////////////////////////////////////////////////////////////

    fun resetPaginationState() {
        updateState(
            paginationState = PaginationState(
                currentPage = 1,
                items = emptyList()
            )
        )
    }


    private fun observeBreakingNews() {
        viewModelScope.launch {
            getBreakingNewsUseCase.state.collect { breakingNewsState ->
                updateState(
                    isLoading = breakingNewsState.isLoading,
                    errorMessage = breakingNewsState.errorMessage,
                    paginationState = breakingNewsState.paginationState
                )
            }
        }
    }

    private fun observeSearchingNews() {
        viewModelScope.launch {
            getSearchNewsUseCase.state.collect { searchNewsState ->
                updateState(
                    isLoading = searchNewsState.isLoading,
                    errorMessage = searchNewsState.errorMessage,
                    paginationState = searchNewsState.paginationState
                )
            }
        }
    }

    private fun observeArticle() {
        viewModelScope.launch {
            getArticleUseCase.state.collect { articleState ->
                updateState(
                    isArticleSaved = articleState.isArticleSaved
                )
            }
        }
    }

    private fun observeSavedNews() {
        viewModelScope.launch {
            getSavedNewsUseCase.state.collect { savedNewsState ->
                updateState(
                    isLoading = savedNewsState.isLoading,
                    errorMessage = savedNewsState.errorMessage,
                    paginationState = PaginationState(items = savedNewsState.savedNewsList ?: emptyList())
                )
            }
        }
    }

    fun handleToggleMenu() {
        updateState(isShowMenu = !_state.value.isShowMenu)
    }

    fun handleNightMode() {
        updateState(isDarkMode = !_state.value.isDarkMode)
    }


    private fun updateState(
        isLoading: Boolean? = null,
        errorMessage: String? = null,
        isShowMenu: Boolean? = null,
        isDarkMode: Boolean? = null,
        breakingNews: NewsResponse? = null,
        paginationState: PaginationState? = null,
        isArticleSaved: Boolean? = null
    ) {
        _state.update { currentState ->
            currentState.copy(
                isLoading = isLoading ?: currentState.isLoading,
                errorMessage = errorMessage ?: currentState.errorMessage,
                isShowMenu = isShowMenu ?: currentState.isShowMenu,
                isDarkMode = isDarkMode ?: currentState.isDarkMode,
                breakingNews = breakingNews ?: currentState.breakingNews,
                paginationState = paginationState ?: currentState.paginationState,
                isArticleSaved = isArticleSaved
            )
        }
    }
}