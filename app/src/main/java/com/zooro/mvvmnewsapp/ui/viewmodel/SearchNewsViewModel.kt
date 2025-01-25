package com.zooro.mvvmnewsapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.zooro.mvvmnewsapp.domain.model.Article
import com.zooro.mvvmnewsapp.domain.repository.NewsApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchNewsViewModel(
    private val pagingRepository: NewsApiRepository
) : ViewModel() {

    private val _searchNewsData = MutableStateFlow<PagingData<Article>>(PagingData.empty())
    val searchNewsData = _searchNewsData.asStateFlow()

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
                .filterNot { it.isEmpty() }
                .distinctUntilChanged()
                .filter { it.length >= 3 }
                .collectLatest { query ->
                    withContext(Dispatchers.IO) {
                        pagingRepository.getSearchNews(query)
                            .cachedIn(viewModelScope)
                            .collect { pagingData ->
                                updateData(
                                    data = pagingData,
                                )
                            }
                    }
                }
        }
    }

    private fun updateData(
        data: PagingData<Article>,
    ) {
        _searchNewsData.value = data
    }
}