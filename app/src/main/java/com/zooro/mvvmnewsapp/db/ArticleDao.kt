package com.zooro.mvvmnewsapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.zooro.mvvmnewsapp.models.Article

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article): Long

    @Delete
    suspend fun deleteArticle(article: Article)

    @Query("SELECT * FROM articles")
    fun getAllArticles(): LiveData<List<Article>>

    @Query("SELECT COUNT(*) FROM articles WHERE url=:url")
    suspend fun getArticleUrl(url: String) : Int
}