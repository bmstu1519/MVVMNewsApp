package com.zooro.mvvmnewsapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zooro.mvvmnewsapp.domain.model.Article
import com.zooro.mvvmnewsapp.domain.repository.ArticleRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class SavedNewsViewModel(
    private val articleRepository: ArticleRepository
) : ViewModel() {

    val articles = articleRepository.getSavedArticles()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteArticle(article: Article) {
        viewModelScope.launch {
            articleRepository.deleteArticle(article)
        }
    }
}