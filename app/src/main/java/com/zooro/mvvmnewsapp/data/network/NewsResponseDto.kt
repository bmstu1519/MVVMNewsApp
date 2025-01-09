package com.zooro.mvvmnewsapp.data.network

import com.zooro.mvvmnewsapp.data.db.ArticleDto

data class NewsResponseDto(
    val articles: MutableList<ArticleDto>,
    val status: String,
    val totalResults: Int
)