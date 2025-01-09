package com.zooro.mvvmnewsapp.domain.model

data class Article(
    var id: Int? = null,
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val newsSource: NewsSource?,
    val title: String?,
    val url: String?,
    val urlToImage: String?
)