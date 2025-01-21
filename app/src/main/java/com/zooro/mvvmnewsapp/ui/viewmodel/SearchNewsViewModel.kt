package com.zooro.mvvmnewsapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.zooro.mvvmnewsapp.domain.model.Article
import com.zooro.mvvmnewsapp.domain.repository.NetworkHelperRepository
import com.zooro.mvvmnewsapp.domain.repository.PaginationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.launch


class SearchNewsViewModel(
    private val networkHelper: NetworkHelperRepository,
    private val pagingRepository: PaginationRepository
) : ViewModel() {

    private val _searchResults = MutableStateFlow<PagingData<Article>>(PagingData.empty())
    val searchResults = _searchResults.asStateFlow()
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
                .collectLatest  { query ->
                    pagingRepository.getSearchNews(query)
                        .cachedIn(viewModelScope)
                        .collect { pagingData ->
                            _searchResults.value = pagingData
                        }
                }
        }
    }
}