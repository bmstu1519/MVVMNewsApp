package com.zooro.mvvmnewsapp.domain.model

import com.zooro.mvvmnewsapp.data.db.ArticleDto

data class NewsResponse(
    val articles: MutableList<ArticleDto>,
    val status: String,
    val totalResults: Int
)