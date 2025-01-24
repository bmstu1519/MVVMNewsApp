package com.zooro.mvvmnewsapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.zooro.mvvmnewsapp.domain.model.Article
import com.zooro.mvvmnewsapp.domain.repository.NewsApiRepository
import com.zooro.mvvmnewsapp.ui.util.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

class SearchNewsViewModel(
    private val pagingRepository: NewsApiRepository
) : ViewModel() {

    private val _state = MutableStateFlow(UiState<PagingData<Article>>())
    val state = _state.asStateFlow()

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
                    updateState(
                        isLoading = true
                    )
                }
                .filterNot { it.isEmpty() }
                .distinctUntilChanged()
                .filter { it.length >= 3 }
                .collectLatest { query ->
                    try {
                        withContext(Dispatchers.IO) {
                            pagingRepository.getSearchNews(query)
                                .cachedIn(viewModelScope)
                                .collect { pagingData ->
                                    updateState(
                                        data = pagingData,
                                        isLoading = false,
                                        errorMessage = null
                                    )
                                }
                        }
                    } catch (e: Exception) {
                        if (e !is CancellationException) {
                            updateState(
                                data = null,
                                isLoading = false,
                                errorMessage = e.localizedMessage
                            )
                        }
                    }
                }
        }
    }

    private suspend fun updateState(
        data: PagingData<Article>? = null,
        isLoading: Boolean = false,
        errorMessage: String? = null
    ) {
        withContext(Dispatchers.Main) {
            _state.update { current ->
                current.copy(
                    data = data ?: current.data,
                    isLoading = isLoading,
                    errorMessage = errorMessage
                )
            }
        }
    }
}