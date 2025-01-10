package com.zooro.mvvmnewsapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zooro.mvvmnewsapp.data.toDomain
import com.zooro.mvvmnewsapp.data.toNetwork
import com.zooro.mvvmnewsapp.domain.model.Article
import com.zooro.mvvmnewsapp.domain.model.NewsResponse
import com.zooro.mvvmnewsapp.domain.model.Resource
import com.zooro.mvvmnewsapp.domain.repository.NetworkHelper
import com.zooro.mvvmnewsapp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class NewsState {
    data object Initial : NewsState()
    data object Loading : NewsState()
    data class Content(
        val isShowMenu: Boolean = false,
        val isDarkMode: Boolean = false
    ) : NewsState()
    data class Error(val message: String) : NewsState()
}

class NewsViewModelV2(
    private val newsRepository: NewsRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val _state = MutableStateFlow<NewsState>(NewsState.Initial)
    val state: StateFlow<NewsState> = _state.asStateFlow()

    private val _breakingNews = MutableStateFlow<Resource<NewsResponse>>(Resource.Loading())
    val breakingNews: StateFlow<Resource<NewsResponse>> = _breakingNews.asStateFlow()

    private val _searchNews = MutableStateFlow<Resource<NewsResponse>>(Resource.Loading())
    val searchNews: StateFlow<Resource<NewsResponse>> = _searchNews.asStateFlow()

    var breakingNewsPage = 1
    var searchNewsPage = 1

    init {
        getBreakingNews("us")
    }

    fun handleToggleMenu() {
        _state.update { currentState ->
            when (currentState) {
                is NewsState.Content -> currentState.copy(isShowMenu = !currentState.isShowMenu)
                else -> NewsState.Content(isShowMenu = true)
            }
        }
    }

    fun handleNightMode() {
        _state.update { currentState ->
            when (currentState) {
                is NewsState.Content -> currentState.copy(isDarkMode = !currentState.isDarkMode)
                else -> NewsState.Content(isDarkMode = true)
            }
        }
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        _breakingNews.value = Resource.Loading()

        try {
            if (networkHelper.isNetworkConnected()) {
                newsRepository.getBreakingNews(countryCode, breakingNewsPage)
                    .onSuccess { newsResponseDto ->
                        breakingNewsPage++
                        _breakingNews.value = Resource.Success(newsResponseDto.toDomain())
                    }
                    .onFailure { exception ->
                        _breakingNews.value = Resource.Error(exception.message ?: "Unknown error")
                    }
            } else {
                _breakingNews.value = Resource.Error("No internet connection")
            }
        } catch (e: Exception) {
            _breakingNews.value = Resource.Error(e.localizedMessage ?: "Unknown error")
        }
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        _searchNews.value = Resource.Loading()

        try {
            if (networkHelper.isNetworkConnected()) {
                newsRepository.searchNews(searchQuery, searchNewsPage)
                    .onSuccess { newsResponseDto ->
                        searchNewsPage++
                        _searchNews.value = Resource.Success(newsResponseDto.toDomain())
                    }
                    .onFailure { exception ->
                        _searchNews.value = Resource.Error(exception.message ?: "Unknown error")
                    }
            } else {
                _searchNews.value = Resource.Error("No internet connection")
            }
        } catch (e: Exception) {
            _searchNews.value = Resource.Error(e.localizedMessage ?: "Unknown error")
        }
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
}