package com.zooro.mvvmnewsapp.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveArticle(article: ArticleDto): Long

    @Delete
    suspend fun deleteArticle(article: ArticleDto)

    @Query("SELECT * FROM articles")
    fun getSavedArticles(): Flow<List<ArticleDto>>

    @Query("SELECT COUNT(*) FROM articles WHERE url=:url")
    suspend fun getArticleUrl(url: String) : Int
}