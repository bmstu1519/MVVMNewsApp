package com.zooro.mvvmnewsapp.domain.repository

import com.zooro.mvvmnewsapp.data.api.RetrofitInstance
import com.zooro.mvvmnewsapp.data.db.ArticleDatabase
import com.zooro.mvvmnewsapp.data.models.ArticleDto

class NewsRepository(
    val db: ArticleDatabase
) {
    suspend fun  getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery,pageNumber)

    suspend fun upsert(article: ArticleDto) = db.getArticleDao().upsert(article)

    suspend fun deleteArticle(article: ArticleDto) = db.getArticleDao().deleteArticle(article)

    fun getSavedNews() = db.getArticleDao().getAllArticles()

    suspend fun checkSavedArticle(url: String) : Int = db.getArticleDao().getArticleUrl(url)
}