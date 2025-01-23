package com.zooro.mvvmnewsapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.zooro.mvvmnewsapp.domain.model.Article
import com.zooro.mvvmnewsapp.domain.repository.NewsApiRepository
import com.zooro.mvvmnewsapp.ui.util.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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
        newsJob = viewModelScope.launch {
            pagingRepository.getBreakingNews("us")
                .cachedIn(viewModelScope)
                .catch { e ->
                    _state.value = UiState(
                        data = null,
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
                .collectLatest { pagingData ->
                    _state.value = UiState(
                        data = pagingData,
                        isLoading = false,
                        errorMessage = null
                    )
                }
        }
    }
}