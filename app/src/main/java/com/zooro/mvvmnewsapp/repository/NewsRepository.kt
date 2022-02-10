package com.zooro.mvvmnewsapp.repository

import com.zooro.mvvmnewsapp.api.RetrofitInstance
import com.zooro.mvvmnewsapp.db.ArticleDatabase
import com.zooro.mvvmnewsapp.models.Article

class NewsRepository(
    val db: ArticleDatabase
) {
    suspend fun  getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery,pageNumber)

    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)

    fun getSavedNews() = db.getArticleDao().getAllArticles()

    suspend fun checkSavedArticle(url: String) : Int = db.getArticleDao().getArticleUrl(url)
}