package com.zooro.mvvmnewsapp.domain.model

data class PaginationState(
    val isLoading: Boolean = false,
    val isLastPage: Boolean = false,
    val currentPage: Int = 1,
    val items: List<Article> = emptyList()
)