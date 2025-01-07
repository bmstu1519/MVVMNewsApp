package com.zooro.mvvmnewsapp.domain.repository

import com.zooro.mvvmnewsapp.data.models.ArticleDto
import com.zooro.mvvmnewsapp.di.DependencyProvider

class NewsRepository(
    private val deps: DependencyProvider
) {
    suspend fun  getBreakingNews(countryCode: String, pageNumber: Int) =
        deps.retrofitClient.getBreakingNews(countryCode, pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        deps.retrofitClient.searchForNews(searchQuery,pageNumber)

    suspend fun upsert(article: ArticleDto) = deps.roomClient.getArticleDao().upsert(article)

    suspend fun deleteArticle(article: ArticleDto) = deps.roomClient.getArticleDao().deleteArticle(article)

    fun getSavedNews() = deps.roomClient.getArticleDao().getAllArticles()

    suspend fun checkSavedArticle(url: String) : Int = deps.roomClient.getArticleDao().getArticleUrl(url)
}