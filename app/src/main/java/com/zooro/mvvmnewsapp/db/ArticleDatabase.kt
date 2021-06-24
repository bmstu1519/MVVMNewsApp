package com.zooro.mvvmnewsapp.db

import android.content.Context
import androidx.room.*
import com.zooro.mvvmnewsapp.models.Article

@Database(
    entities = [Article::class],
    version = 1
)

@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase() {

    abstract fun getArticleDao(): ArticleDao

    companion object{
        @Volatile
        private var instance: ArticleDatabase? = null
        private val LOCK = Any()

        //создается единственный экземпляр базы данных, если текущий экземпляр бд = null
        //то вызываю блок синхронизации, в котором возвращается экземпляр БД
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: createDatabase(context).also { instance = it}
        }

        private fun createDatabase(context: Context)=
            Room.databaseBuilder(
                context.applicationContext,
                ArticleDatabase::class.java,
                "article_db.db"
            ).build()
    }
}