package com.zooro.mvvmnewsapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zooro.mvvmnewsapp.domain.model.Article
import com.zooro.mvvmnewsapp.domain.repository.ArticleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ArticleNewsViewModel(
    private val articleRepository: ArticleRepository
) : ViewModel() {

    private val _snackBarMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    val snackBarMessage = _snackBarMessage.asStateFlow()

    fun tryToSaveArticle(article: Article) {
        viewModelScope.launch {
            article.url?.let { url ->
                val result = articleRepository.isArticleSaved(url)
                when (result) {
                    true -> updateState(
                        data = "You are already save this article"
                    )

                    false -> saveArticle(article)
                }
            }
            updateState(
                data = null,
            )
        }
    }

    private suspend fun saveArticle(article: Article) {
        articleRepository.saveArticle(article)
        updateState(
            data = "Article saved successfully"
        )
    }

    private fun updateState(
        data: String?,
    ) {
        _snackBarMessage.value = data
    }
}