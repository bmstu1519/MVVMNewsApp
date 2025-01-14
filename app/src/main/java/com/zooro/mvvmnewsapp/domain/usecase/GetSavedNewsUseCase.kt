package com.zooro.mvvmnewsapp.domain.usecase

import com.zooro.mvvmnewsapp.domain.model.Article
import com.zooro.mvvmnewsapp.domain.repository.ArticleUseCaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update

data class SavedNewsState(
    val isLoading: Boolean = false,
    val savedNewsList: List<Article>? = null,
    val errorMessage: String? = null
)

class GetSavedNewsUseCase(
    private val articleUseCaseRepository: ArticleUseCaseRepository
) {
    private val _state = MutableStateFlow(SavedNewsState())
    val state = _state.asStateFlow()

    operator fun invoke(): Flow<List<Article>> =
        articleUseCaseRepository.getSavedArticles()
        .onStart {
            updateState(isLoading = true)
        }
        .onEach { articles ->
            updateState(
                isLoading = false,
                savedNewsList = articles
            )
        }
        .catch { e ->
            updateState(
                isLoading = false,
                savedNewsList = emptyList(),
                errorMessage = e.message
            )
        }

    private fun updateState(
        isLoading: Boolean,
        savedNewsList: List<Article>? = null,
        errorMessage: String? = null
    ) {
        _state.update { currentState ->
            currentState.copy(
                isLoading = isLoading,
                savedNewsList = savedNewsList ?: currentState.savedNewsList,
                errorMessage = errorMessage ?: currentState.errorMessage,
            )
        }
    }
}