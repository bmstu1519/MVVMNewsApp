package com.zooro.mvvmnewsapp.data

import com.zooro.mvvmnewsapp.data.db.ArticleDto
import com.zooro.mvvmnewsapp.data.db.NewsSourceDto
import com.zooro.mvvmnewsapp.domain.model.Article
import com.zooro.mvvmnewsapp.domain.model.NewsSource

fun Article.toNetwork() = ArticleDto(
    id = id ,
    author = author ,
    content = content ,
    description = description ,
    publishedAt = publishedAt ,
    newsSource = newsSource?.toNetwork() ,
    title = title ,
    url = url ,
    urlToImage = urlToImage ,
)

fun ArticleDto.toLocal() = Article(
    id = id ,
    author = author ,
    content = content ,
    description = description ,
    publishedAt = publishedAt ,
    newsSource = newsSource?.toLocal() ,
    title = title ,
    url = url ,
    urlToImage = urlToImage ,
)

fun NewsSource.toNetwork() = NewsSourceDto(
    id = id,
    name = name,
)

fun NewsSourceDto.toLocal() = NewsSource(
    id = id,
    name = name,
)