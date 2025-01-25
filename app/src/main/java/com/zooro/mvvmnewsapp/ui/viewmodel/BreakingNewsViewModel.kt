package com.zooro.mvvmnewsapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.zooro.mvvmnewsapp.domain.model.Article
import com.zooro.mvvmnewsapp.domain.repository.NewsApiRepository
import com.zooro.mvvmnewsapp.ui.util.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BreakingNewsViewModel(
    private val pagingRepository: NewsApiRepository
) : ViewModel() {

    private val _state = MutableStateFlow(UiState<PagingData<Article>>(isLoading = true))
    val state = _state.asStateFlow()

    private var newsJob: Job? = null

    init {
        observeBreakingNews()
    }

    fun refresh() {
        newsJob?.cancel()
        observeBreakingNews()
    }

    private fun observeBreakingNews() {
        newsJob = viewModelScope.launch(Dispatchers.IO) {
            pagingRepository.getBreakingNews("us")
                .cachedIn(viewModelScope)
                .catch { e ->
                    updateState(
                        data = null,
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
                .collect { pagingData ->
                    updateState(
                        data = pagingData,
                        isLoading = false,
                        errorMessage = null
                    )
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