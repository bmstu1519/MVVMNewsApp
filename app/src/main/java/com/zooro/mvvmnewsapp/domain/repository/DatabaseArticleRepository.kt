package com.zooro.mvvmnewsapp.domain.repository

import com.zooro.mvvmnewsapp.domain.model.Article
import kotlinx.coroutines.flow.Flow

interface DatabaseArticleRepository {

    suspend fun saveArticle(article: Article)

    suspend fun deleteArticle(article: Article)

    suspend fun isArticleSaved(url: String): Boolean

    fun getSavedArticles(): Flow<List<Article>>
}