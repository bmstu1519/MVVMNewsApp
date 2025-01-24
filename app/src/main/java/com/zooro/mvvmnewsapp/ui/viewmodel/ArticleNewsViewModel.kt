package com.zooro.mvvmnewsapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zooro.mvvmnewsapp.domain.model.Article
import com.zooro.mvvmnewsapp.domain.repository.ArticleRepository
import com.zooro.mvvmnewsapp.ui.util.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ArticleState(
    val snackBarMessage: String? = null
)

class ArticleNewsViewModel(
    private val articleRepository: ArticleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(UiState<ArticleState>())
    val state = _state.asStateFlow()

    fun tryToSaveArticle(article: Article) {
        viewModelScope.launch {
            article.url?.let { url ->
                val result = articleRepository.isArticleSaved(url)
                when (result) {
                    true -> updateState(
                        ArticleState(
                            snackBarMessage = "You are already save this article",
                        )
                    )
                    false -> saveArticle(article)
                }
            }
            updateState(
                ArticleState(
                    snackBarMessage = null,
                )
            )
        }
    }

    private suspend fun saveArticle(article: Article) {
        articleRepository.saveArticle(article)
        updateState(
            ArticleState(
                snackBarMessage = "Article saved successfully",
            )
        )
    }

    private suspend fun updateState(
        data: ArticleState? = null,
        isLoading: Boolean = false,
        errorMessage: String? = null
    ) {
        withContext(Dispatchers.Main) {
            _state.update { current ->
                current.copy(
                    data = data ?: current.data,
                    isLoading = isLoading,
                    errorMessage = errorMessage
                )
            }
        }
    }
}