package com.zooro.mvvmnewsapp.repository

import com.zooro.mvvmnewsapp.api.RetrofitInstance
import com.zooro.mvvmnewsapp.db.ArticleDatabase

class NewsRepository(
    val db: ArticleDatabase
) {
    suspend fun  getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)
}