package com.zooro.mvvmnewsapp.data.repository

import com.zooro.mvvmnewsapp.data.db.ArticleDto
import com.zooro.mvvmnewsapp.data.network.NewsApiService
import com.zooro.mvvmnewsapp.data.network.NewsResponseDto
import com.zooro.mvvmnewsapp.di.ArticleDbInterface
import com.zooro.mvvmnewsapp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow

class NewsRepositoryImpl(
    private val api: NewsApiService,
    private val db: ArticleDbInterface,
) : NewsRepository {

    override suspend fun getBreakingNews(
        countryCode: String,
        pageNumber: Int
    ): Result<NewsResponseDto> = runCatching { api.getBreakingNews(countryCode, pageNumber) }
        .fold(
            onSuccess = { response ->
                if (response.isSuccessful) {
                    Result.success(response.body() ?: throw Exception("Response body is null"))
                } else {
                    Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
                }
            },
            onFailure = {
                Result.failure(it)
            }
        )

    override suspend fun searchNews(
        searchQuery: String,
        pageNumber: Int
    ): Result<NewsResponseDto> = runCatching { api.searchForNews(searchQuery, pageNumber) }
        .fold(
            onSuccess = { response ->
                if (response.isSuccessful) {
                    Result.success(response.body() ?: throw Exception("Response body is null"))
                } else {
                    Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
                }
            },
            onFailure = {
                Result.failure(it)
            }
        )

    override suspend fun saveArticle(article: ArticleDto): Long =
        db.getArticleDao().saveArticle(article)

    override suspend fun deleteArticle(article: ArticleDto) =
        db.getArticleDao().deleteArticle(article)

    override fun getSavedNews(): Flow<List<ArticleDto>> = db.getArticleDao().getAllArticles()

    override suspend fun isArticleSaved(url: String): Int = db.getArticleDao().getArticleUrl(url)

    override fun getNewsPagingFlow(searchQuery: String): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                enablePlaceholders = false,
                maxSize = ITEMS_PER_PAGE * 3
            ),
            pagingSourceFactory = {
                ArticlePagingSource(this, searchQuery)
            }
        ).flow
    }

    companion object {
        const val ITEMS_PER_PAGE = 20
    }
}