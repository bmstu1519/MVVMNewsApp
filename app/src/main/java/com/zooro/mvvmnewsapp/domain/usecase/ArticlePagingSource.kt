package com.zooro.mvvmnewsapp.domain.usecase

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.zooro.mvvmnewsapp.data.toDomain
import com.zooro.mvvmnewsapp.domain.model.Article
import com.zooro.mvvmnewsapp.domain.repository.NewsRepository

class ArticlePagingSource(
//    private val newsRepository: NewsRepository,
//    private val searchQuery: String,
    private val articleList: List<Article>,
) : PagingSource<Int, Article>() {

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val page = params.key ?: 1

        return LoadResult.Page(
            data = articleList,
            prevKey = if (page == 1) null else page - 1,
            nextKey = if (articleList.isEmpty()) null else page + 1
        )
//        return try {
//            val page = params.key ?: 1
//
//            val response = newsRepository.searchNews(searchQuery, page)
//
//            response.fold(
//                onSuccess = { newsResponseDto ->
//                    val articles = newsResponseDto.articles.map { it.toDomain() }
//
//                    LoadResult.Page(
//                        data = articles,
//                        prevKey = if (page == 1) null else page - 1,
//                        nextKey = if (articles.isEmpty()) null else page + 1
//                    )
//                },
//                onFailure = { throwable ->
//                    LoadResult.Error(throwable)
//                }
//            )
//        } catch (e: Exception) {
//            LoadResult.Error(e)
//        }
    }
}