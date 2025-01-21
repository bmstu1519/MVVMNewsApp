package com.zooro.mvvmnewsapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.zooro.mvvmnewsapp.domain.model.Article
import com.zooro.mvvmnewsapp.domain.repository.NewsRepository
import com.zooro.mvvmnewsapp.domain.repository.PaginationRepository
import com.zooro.mvvmnewsapp.domain.usecase.ArticlePagingSource
import kotlinx.coroutines.flow.Flow

class PaginationRepositoryImpl(
    private val newsRepository: NewsRepository
): PaginationRepository {

    override fun getSearchNews(inputQuery: String): Flow<PagingData<Article>> {
        return Pager(PagingConfig(ITEMS_PER_PAGE)) {
            ArticlePagingSource { page ->
                newsRepository.searchNews(inputQuery, page)
            }
        }.flow
    }

    override fun getBreakingNews(countryCode: String): Flow<PagingData<Article>> {
        return Pager(PagingConfig(ITEMS_PER_PAGE)) {
            ArticlePagingSource { page ->
                newsRepository.getBreakingNews(countryCode, page)
            }
        }.flow
    }


    companion object {
        const val ITEMS_PER_PAGE = 5
    }
}