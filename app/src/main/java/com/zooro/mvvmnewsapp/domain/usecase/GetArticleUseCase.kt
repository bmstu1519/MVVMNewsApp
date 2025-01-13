package com.zooro.mvvmnewsapp.domain.usecase

import androidx.lifecycle.viewModelScope
import com.zooro.mvvmnewsapp.data.toDomain
import com.zooro.mvvmnewsapp.data.toNetwork
import com.zooro.mvvmnewsapp.domain.model.Article
import com.zooro.mvvmnewsapp.domain.repository.ArticleUseCaseRepository
import com.zooro.mvvmnewsapp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class ArticleState(
    val isArticleSaved: Boolean? = null
)

class GetArticleUseCase(
    private val newsRepository: NewsRepository,
): ArticleUseCaseRepository {

    operator fun invoke(
        article: Article
    ): Flow<ArticleState> {

    }

    fun checkArticleSaved(article: Article) = viewModelScope.launch {
        val isSaved = article.url?.let { url ->
            isArticleSaved(url)
        }
        when(isSaved){
            true -> updateState(isArticleSaved = isSaved)
            false -> {
                saveArticle(article)
                updateState(isArticleSaved = isSaved)
            }
            null -> {
                /*nothing to do */
            }
        }
    }

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