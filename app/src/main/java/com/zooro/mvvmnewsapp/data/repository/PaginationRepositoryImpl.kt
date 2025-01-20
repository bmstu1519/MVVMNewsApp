package com.zooro.mvvmnewsapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.zooro.mvvmnewsapp.domain.model.Article
import com.zooro.mvvmnewsapp.domain.repository.PaginationRepository
import com.zooro.mvvmnewsapp.domain.usecase.ArticlePagingSource
import kotlinx.coroutines.flow.Flow

class PaginationRepositoryImpl(

): PaginationRepository {
    override fun getNewsPagingFlow(items: List<Article>): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                enablePlaceholders = false,
//                maxSize = ITEMS_PER_PAGE * 3
            ),
            pagingSourceFactory = {
                ArticlePagingSource(articleList = items)
            }
        ).flow
    }



    companion object {
        const val ITEMS_PER_PAGE = 5
    }
}