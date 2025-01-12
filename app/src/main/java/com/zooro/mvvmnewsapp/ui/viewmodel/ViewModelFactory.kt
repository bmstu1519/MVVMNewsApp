package com.zooro.mvvmnewsapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zooro.mvvmnewsapp.domain.repository.NetworkHelperRepository
import com.zooro.mvvmnewsapp.domain.repository.NewsRepository

class ViewModelFactory(
    private val newsRepository: NewsRepository,
    private val networkHelper: NetworkHelperRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(NewsViewModel::class.java) -> {
                NewsViewModel(newsRepository, networkHelper) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}