package com.zooro.mvvmnewsapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zooro.mvvmnewsapp.domain.repository.ArticleRepository
import com.zooro.mvvmnewsapp.domain.repository.NetworkHelperRepository
import com.zooro.mvvmnewsapp.domain.repository.NewsApiRepository

class ViewModelFactory(
    private val networkHelper: NetworkHelperRepository,
    private val pagingRepository: NewsApiRepository,
    private val articleRepository: ArticleRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SearchNewsViewModel::class.java) -> {
                SearchNewsViewModel(
                    pagingRepository
                ) as T
            }

            modelClass.isAssignableFrom(BreakingNewsViewModel::class.java) -> {
                BreakingNewsViewModel(
                    pagingRepository
                ) as T
            }
            modelClass.isAssignableFrom(SavedNewsViewModel::class.java) -> {
                SavedNewsViewModel(
                    articleRepository
                ) as T
            }
            modelClass.isAssignableFrom(ArticleNewsViewModel::class.java) -> {
                ArticleNewsViewModel(
                    articleRepository
                ) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}