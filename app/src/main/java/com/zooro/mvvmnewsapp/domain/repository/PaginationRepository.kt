package com.zooro.mvvmnewsapp.domain.repository

import androidx.paging.PagingData
import com.zooro.mvvmnewsapp.domain.model.Article
import kotlinx.coroutines.flow.Flow

interface PaginationRepository {
    fun getNewsPagingFlow(items: List<Article>): Flow<PagingData<Article>>
}