package com.zooro.mvvmnewsapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zooro.mvvmnewsapp.domain.usecase.GetArticleUseCase
import com.zooro.mvvmnewsapp.domain.usecase.GetBreakingNewsUseCase
import com.zooro.mvvmnewsapp.domain.usecase.GetSavedNewsUseCase
import com.zooro.mvvmnewsapp.domain.usecase.GetSearchNewsUseCase

class ViewModelFactory(
    private val getBreakingNewsUseCase: GetBreakingNewsUseCase,
    private val getSearchNewsUseCase: GetSearchNewsUseCase,
    private val getArticleUseCase: GetArticleUseCase,
    private val getSavedNewsUseCase: GetSavedNewsUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(NewsViewModel::class.java) -> {
                NewsViewModel(
                    getBreakingNewsUseCase,
                    getSearchNewsUseCase,
                    getArticleUseCase,
                    getSavedNewsUseCase
                ) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}