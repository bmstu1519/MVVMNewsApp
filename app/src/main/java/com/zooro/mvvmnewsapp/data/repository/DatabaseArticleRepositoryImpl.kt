package com.zooro.mvvmnewsapp.data.repository

import android.util.Log
import com.zooro.mvvmnewsapp.data.toDomain
import com.zooro.mvvmnewsapp.data.toNetwork
import com.zooro.mvvmnewsapp.domain.model.Article
import com.zooro.mvvmnewsapp.domain.repository.DatabaseArticleRepository
import com.zooro.mvvmnewsapp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class DatabaseArticleRepositoryImpl(
    private val newsRepository: NewsRepository,
) : DatabaseArticleRepository {

    override suspend fun saveArticle(article: Article) {
        newsRepository.saveArticle(article.toNetwork())
    }

    override suspend fun deleteArticle(article: Article) {
        newsRepository.deleteArticle(article.toNetwork())
    }

    override suspend fun isArticleSaved(url: String): Boolean {
        return newsRepository.isArticleSaved(url) > 0
    }

    override fun getSavedArticles(): Flow<List<Article>> =
        newsRepository.getSavedNews()
            .map { articles -> articles.map { it.toDomain() } }
            .onEach {
            Log.d("Repository", "Thread: ${Thread.currentThread().name}")
        }
//            .flowOn(Dispatchers.IO)
}