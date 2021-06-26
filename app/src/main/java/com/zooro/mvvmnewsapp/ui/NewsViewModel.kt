package com.zooro.mvvmnewsapp.ui

import androidx.lifecycle.ViewModel
import com.zooro.mvvmnewsapp.repository.NewsRepository

class NewsViewModel(
    val newsRepository: NewsRepository
): ViewModel() {

}