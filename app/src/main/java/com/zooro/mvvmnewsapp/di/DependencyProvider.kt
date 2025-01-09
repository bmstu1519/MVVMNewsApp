package com.zooro.mvvmnewsapp.di

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zooro.mvvmnewsapp.data.network.ApiSettings.Companion.BASE_URL
import com.zooro.mvvmnewsapp.data.network.NewsApiService
import com.zooro.mvvmnewsapp.data.db.ArticleDao
import com.zooro.mvvmnewsapp.data.db.Converters
import com.zooro.mvvmnewsapp.data.db.ArticleDto
import com.zooro.mvvmnewsapp.data.repository.NewsRepositoryImpl
import com.zooro.mvvmnewsapp.domain.repository.NewsRepository
import com.zooro.mvvmnewsapp.ui.viewmodels.ViewModelFactory
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
    private val retrofitClient: NewsApiService = RetrofitProvider.newsApiService
    private val roomClient: ArticleDbInterface by lazy {
        DatabaseProvider(applicationContext)
    }

    //repositories
    private val newsRepository: NewsRepository by lazy {
        NewsRepositoryImpl(retrofitClient, roomClient)
    }

    val viewModelFactory: ViewModelFactory by lazy {
        ViewModelFactory(applicationContext as Application, newsRepository)
    }

    fun provideNewsRepository(): NewsRepository = newsRepository
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