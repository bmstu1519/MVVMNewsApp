package com.zooro.mvvmnewsapp.domain.usecase

import com.zooro.mvvmnewsapp.domain.model.Article
import com.zooro.mvvmnewsapp.domain.repository.ArticleUseCaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update

data class ArticleState(
    val isArticleSaved: Boolean? = null
)

class GetArticleUseCase(
    private val articleUseCaseRepository: ArticleUseCaseRepository
){
    private val _state = MutableStateFlow(ArticleState())
    val state = _state.asStateFlow()

    operator fun invoke(
        article: Article
    ): Flow<ArticleState> = flow {
        try {
            val isSaved = article.url?.let { url ->
                articleUseCaseRepository.isArticleSaved(url)
            }
            when(isSaved){
                true -> updateState(isArticleSaved = isSaved)
                false -> {
                    articleUseCaseRepository.saveArticle(article)
                    updateState(isArticleSaved = isSaved)
                }
                null -> {
                    /*nothing to do */
                }
            }
        } catch (e: Exception) {

        }
    }

    private fun updateState(
        isArticleSaved: Boolean? = null
    ) {
        _state.update { currentState ->
            currentState.copy(
                isArticleSaved = isArticleSaved ?: currentState.isArticleSaved
            )
        }
    }
}