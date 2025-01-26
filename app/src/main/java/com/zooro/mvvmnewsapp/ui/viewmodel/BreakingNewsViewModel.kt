package com.zooro.mvvmnewsapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.zooro.mvvmnewsapp.domain.model.Article
import com.zooro.mvvmnewsapp.domain.repository.NewsApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class BreakingNewsViewModel(
    private val pagingRepository: NewsApiRepository
) : ViewModel() {

    private val _breakingNewsData = MutableStateFlow<PagingData<Article>>(PagingData.empty())
    val breakingNewsData = _breakingNewsData.asStateFlow()

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
                .collect { pagingData ->
                    updateData(
                        data = pagingData
                    )
                }
        }
    }

    private fun updateData(
        data: PagingData<Article>
    ) {
        _breakingNewsData.value = data
    }
}