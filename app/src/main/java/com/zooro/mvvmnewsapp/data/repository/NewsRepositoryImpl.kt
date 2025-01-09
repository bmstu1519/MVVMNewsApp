package com.zooro.mvvmnewsapp.data.repository

import androidx.lifecycle.LiveData
import com.zooro.mvvmnewsapp.data.network.NewsApiService
import com.zooro.mvvmnewsapp.data.db.ArticleDto
import com.zooro.mvvmnewsapp.data.network.NewsResponseDto
import com.zooro.mvvmnewsapp.di.ArticleDbInterface
import com.zooro.mvvmnewsapp.domain.repository.NewsRepository
import retrofit2.Response

class NewsRepositoryImpl(
    private val api: NewsApiService,
    private val db: ArticleDbInterface,
) : NewsRepository {

    override suspend fun getBreakingNews(
        countryCode: String,
        pageNumber: Int
    ): Response<NewsResponseDto> =
        api.getBreakingNews(countryCode, pageNumber)

    override suspend fun searchNews(
        searchQuery: String,
        pageNumber: Int
    ): Response<NewsResponseDto> =
        api.searchForNews(searchQuery, pageNumber)

    override suspend fun upsert(article: ArticleDto): Long = db.getArticleDao().upsert(article)

    override suspend fun deleteArticle(article: ArticleDto) =
        db.getArticleDao().deleteArticle(article)

    override fun getSavedNews(): LiveData<List<ArticleDto>> = db.getArticleDao().getAllArticles()

    override suspend fun checkSavedArticle(url: String): Int = db.getArticleDao().getArticleUrl(url)
}