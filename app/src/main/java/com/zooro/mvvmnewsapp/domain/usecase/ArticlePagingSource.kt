package com.zooro.mvvmnewsapp.domain.usecase

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.zooro.mvvmnewsapp.data.network.NewsResponseDto
import com.zooro.mvvmnewsapp.data.toDomain
import com.zooro.mvvmnewsapp.domain.model.Article
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ArticlePagingSource(
    private val block: suspend (Int) -> Response<NewsResponseDto>
) : PagingSource<Int, Article>() {

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val page = params.key ?: 1

        return try {
            val response = block(page)
            if (response.isSuccessful) {
                val articles = response.body()?.articles?.toDomain() ?: emptyList()
                LoadResult.Page(
                    data = articles,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (articles.isEmpty()) null else page + 1
                )
            } else {
                LoadResult.Error(HttpException(response))
            }
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}