package com.zooro.mvvmnewsapp.domain.repository

import androidx.lifecycle.LiveData
import com.zooro.mvvmnewsapp.data.models.ArticleDto
import com.zooro.mvvmnewsapp.data.models.NewsResponseDto
import retrofit2.Response

interface NewsRepository {
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int): Response<NewsResponseDto>
    suspend fun searchNews(searchQuery: String, pageNumber: Int): Response<NewsResponseDto>
    suspend fun upsert(article: ArticleDto): Long
    suspend fun deleteArticle(article: ArticleDto)
    fun getSavedNews(): LiveData<List<ArticleDto>>
    suspend fun checkSavedArticle(url: String): Int
}