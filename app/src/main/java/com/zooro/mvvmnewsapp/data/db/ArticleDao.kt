package com.zooro.mvvmnewsapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: ArticleDto): Long

    @Delete
    suspend fun deleteArticle(article: ArticleDto)

    @Query("SELECT * FROM articles")
    fun getAllArticles(): LiveData<List<ArticleDto>>

    @Query("SELECT COUNT(*) FROM articles WHERE url=:url")
    suspend fun getArticleUrl(url: String) : Int
}