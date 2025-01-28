package com.zooro.mvvmnewsapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.zooro.mvvmnewsapp.data.network.NewsApiService
import com.zooro.mvvmnewsapp.domain.model.Article
import com.zooro.mvvmnewsapp.domain.repository.NewsApiRepository
import com.zooro.mvvmnewsapp.domain.usecase.ArticlePagingSourceUseCase
import kotlinx.coroutines.flow.Flow

class NewsApiRepositoryImpl(
    private val api: NewsApiService,
): NewsApiRepository {

    override fun getSearchNews(inputQuery: String): Flow<PagingData<Article>> {
        return Pager(PagingConfig(ITEMS_PER_PAGE)) {
            ArticlePagingSourceUseCase { page ->
                api.searchForNews(inputQuery, page)
            }
        }.flow
    }

    override fun getBreakingNews(countryCode: String): Flow<PagingData<Article>> {
        return Pager(PagingConfig(ITEMS_PER_PAGE)) {
            ArticlePagingSourceUseCase { page ->
                api.getBreakingNews(countryCode, page)
            }
        }.flow
    }


    companion object {
        const val ITEMS_PER_PAGE = 20
    }
}