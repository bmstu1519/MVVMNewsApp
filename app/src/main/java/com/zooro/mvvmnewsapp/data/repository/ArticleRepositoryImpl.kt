package com.zooro.mvvmnewsapp.data.repository

import android.util.Log
import com.zooro.mvvmnewsapp.data.toDomain
import com.zooro.mvvmnewsapp.data.toNetwork
import com.zooro.mvvmnewsapp.di.ArticleDbInterface
import com.zooro.mvvmnewsapp.domain.model.Article
import com.zooro.mvvmnewsapp.domain.repository.ArticleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class ArticleRepositoryImpl(
    private val db: ArticleDbInterface,
) : ArticleRepository {

    override suspend fun saveArticle(article: Article) {
        db.getArticleDao().saveArticle(article.toNetwork())
    }

    override suspend fun deleteArticle(article: Article) {
        db.getArticleDao().deleteArticle(article.toNetwork())
    }

    override suspend fun isArticleSaved(url: String): Boolean {
        return db.getArticleDao().getArticleUrl(url) > 0
    }

    override fun getSavedArticles(): Flow<List<Article>> =
        db.getArticleDao().getSavedArticles()
            .map { articles -> articles.map { it.toDomain() } }
            .onEach {
            Log.d("Repository", "Thread: ${Thread.currentThread().name}")
        }
//            .flowOn(Dispatchers.IO)
}