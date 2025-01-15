package com.zooro.mvvmnewsapp.domain.repository

import com.zooro.mvvmnewsapp.data.db.ArticleDto
import com.zooro.mvvmnewsapp.data.network.NewsResponseDto
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int): Result<NewsResponseDto>
    suspend fun searchNews(searchQuery: String, pageNumber: Int): Result<NewsResponseDto>
    suspend fun saveArticle(article: ArticleDto): Long
    suspend fun deleteArticle(article: ArticleDto)
    fun getSavedNews(): Flow<List<ArticleDto>>
    suspend fun isArticleSaved(url: String): Int
}