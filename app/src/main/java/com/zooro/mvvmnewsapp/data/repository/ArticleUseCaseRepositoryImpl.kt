package com.zooro.mvvmnewsapp.data.repository

import com.zooro.mvvmnewsapp.data.toDomain
import com.zooro.mvvmnewsapp.data.toNetwork
import com.zooro.mvvmnewsapp.domain.model.Article
import com.zooro.mvvmnewsapp.domain.repository.ArticleUseCaseRepository
import com.zooro.mvvmnewsapp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ArticleUseCaseRepositoryImpl(
    private val newsRepository: NewsRepository,
): ArticleUseCaseRepository {

    override suspend fun saveArticle(article: Article) {
        newsRepository.saveArticle(article.toNetwork())
    }

    override suspend fun deleteArticle(article: Article) {
        newsRepository.deleteArticle(article.toNetwork())
    }

    override suspend fun isArticleSaved(url: String): Boolean {
        return newsRepository.isArticleSaved(url) > 0
    }

    override suspend fun getSavedArticles(): Flow<List<Article>> {
        return newsRepository.getSavedNews().map { articles ->
            articles.map { it.toDomain() }
        }
    }
}