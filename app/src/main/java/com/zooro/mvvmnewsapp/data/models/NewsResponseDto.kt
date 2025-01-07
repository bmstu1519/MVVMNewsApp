package com.zooro.mvvmnewsapp.data.models

data class NewsResponseDto(
    val articles: MutableList<ArticleDto>,
    val status: String,
    val totalResults: Int
)