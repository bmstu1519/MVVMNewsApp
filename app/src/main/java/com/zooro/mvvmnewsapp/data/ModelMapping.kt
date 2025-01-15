package com.zooro.mvvmnewsapp.data

import com.zooro.mvvmnewsapp.data.db.ArticleDto
import com.zooro.mvvmnewsapp.data.db.NewsSourceDto
import com.zooro.mvvmnewsapp.data.network.NewsResponseDto
import com.zooro.mvvmnewsapp.domain.model.Article
import com.zooro.mvvmnewsapp.domain.model.NewsResponse
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

fun ArticleDto.toDomain() = Article(
    id = id ,
    author = author ,
    content = content ,
    description = description ,
    publishedAt = publishedAt ,
    newsSource = newsSource?.toDomain() ,
    title = title ,
    url = url ,
    urlToImage = urlToImage ,
)

fun NewsResponse.toNetwork() = NewsResponseDto(
    articles = articles.toNetwork(),
    status = status,
    totalResults = totalResults,
)

fun NewsResponseDto.toDomain() = NewsResponse(
    articles = articles.toDomain(),
    status = status,
    totalResults = totalResults,
)

fun NewsSource.toNetwork() = NewsSourceDto(
    id = id,
    name = name,
)

fun NewsSourceDto.toDomain() = NewsSource(
    id = id,
    name = name,
)

fun List<ArticleDto>.toDomain(): List<Article> =
    map { articleDto ->
        Article(
            id = articleDto.id,
            author = articleDto.author,
            content = articleDto.content,
            description = articleDto.description,
            publishedAt = articleDto.publishedAt,
            newsSource = articleDto.newsSource?.toDomain(),
            title = articleDto.title,
            url = articleDto.url,
            urlToImage = articleDto.urlToImage
        )
    }

fun List<Article>.toNetwork(): List<ArticleDto> =
    map { article ->
        ArticleDto(
            id = article.id,
            author = article.author,
            content = article.content,
            description = article.description,
            publishedAt = article.publishedAt,
            newsSource = article.newsSource?.toNetwork(),
            title = article.title,
            url = article.url,
            urlToImage = article.urlToImage
        )
    }