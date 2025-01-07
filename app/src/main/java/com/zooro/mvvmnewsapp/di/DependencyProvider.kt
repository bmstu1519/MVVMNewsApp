package com.zooro.mvvmnewsapp.di

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zooro.mvvmnewsapp.data.api.ApiSettings.Companion.BASE_URL
import com.zooro.mvvmnewsapp.data.api.NewsApiService
import com.zooro.mvvmnewsapp.data.db.ArticleDao
import com.zooro.mvvmnewsapp.data.db.Converters
import com.zooro.mvvmnewsapp.data.models.ArticleDto
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DependencyProvider {

    private lateinit var applicationContext: Context

    fun init(context: Context) {
        applicationContext = context.applicationContext
    }

    //clients
    val retrofitClient = RetrofitProvider.newsApiService
    val roomClient by lazy {
        DatabaseProvider(applicationContext)
    }
}

private object RetrofitProvider {
    private val retrofit by lazy {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    val newsApiService: NewsApiService by lazy {
        retrofit.create(NewsApiService::class.java)
    }
}
/**
 * интерфейс нужен для того чтобы не нарушать инкапсуляцию абстрактного класса
 * abstract class ArticleDatabase
 * если не использовать интерфейс, а использовать только модификатор private, то:
 * 1) не получится скомпилироваться
 * 2) val roomClient by lazy ломает смысл модификтора private, дает доступ ко всем не заприваченным полям ArticleDatabase
 **/

interface ArticleDbInterface {
    fun getArticleDao(): ArticleDao
}

private object DatabaseProvider {

    @Volatile
    private var database: ArticleDatabase? = null
    private val LOCK = Any()

    operator fun invoke(context: Context): ArticleDbInterface = database ?: synchronized(LOCK) {
        database ?: createDatabase(context).also { database = it }
    }

    private fun createDatabase(context: Context) =
        Room.databaseBuilder(
            context.applicationContext,
            ArticleDatabase::class.java,
            "article_db.db"
        )
//            .fallbackToDestructiveMigration()
            .build()
}

@Database(
    entities = [ArticleDto::class],
    version = 1
)

@TypeConverters(Converters::class)
private abstract class ArticleDatabase : RoomDatabase(), ArticleDbInterface {

    abstract override fun getArticleDao(): ArticleDao
}