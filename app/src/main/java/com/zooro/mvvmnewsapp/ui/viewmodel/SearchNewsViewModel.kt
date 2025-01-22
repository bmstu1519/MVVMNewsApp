package com.zooro.mvvmnewsapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.zooro.mvvmnewsapp.domain.model.Article
import com.zooro.mvvmnewsapp.domain.repository.NewsApiRepository
import com.zooro.mvvmnewsapp.ui.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class SearchNewsViewModel(
    private val pagingRepository: NewsApiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState<PagingData<Article>>())
    val uiState = _uiState.asStateFlow()

    private val currentSearchQuery = MutableStateFlow("")

    init {
        observeSearchingNews()
    }

    fun updateQuery(inputQuery: String?) {
        currentSearchQuery.value = inputQuery ?: ""
    }

    private fun observeSearchingNews() {
        viewModelScope.launch {
            currentSearchQuery
                .onStart {
                    _uiState.value = UiState(
                        isLoading = true
                    )
                }
                .filterNot { it.isEmpty() }
                .distinctUntilChanged()
                .filter { it.length >= 3 }
                .collectLatest { query ->
                    try {
                        pagingRepository.getSearchNews(query)
                            .cachedIn(viewModelScope)
                            .collect { pagingData ->
                                _uiState.value = UiState(
                                    data = pagingData,
                                    isLoading = false,
                                    errorMessage = null
                                )
                            }
                    } catch (e: Exception) {
                        if (e !is CancellationException) {
                            _uiState.value = UiState(
                                data = null,
                                isLoading = false,
                                errorMessage = e.localizedMessage
                            )
                        }
                    }
                }
        }
    }
}