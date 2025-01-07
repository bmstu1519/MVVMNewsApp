package com.zooro.mvvmnewsapp.ui.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zooro.mvvmnewsapp.NewsApplication
import com.zooro.mvvmnewsapp.data.models.ArticleDto
import com.zooro.mvvmnewsapp.data.models.NewsResponseDto
import com.zooro.mvvmnewsapp.domain.repository.NewsRepository
import com.zooro.mvvmnewsapp.domain.model.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(
    app: Application,
    private val newsRepository: NewsRepository
) : BaseViewModel<ArticleState>(app, ArticleState()) {

    val breakingNews: MutableLiveData<Resource<NewsResponseDto>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse: NewsResponseDto? = null

    val searchNews: MutableLiveData<Resource<NewsResponseDto>> = MutableLiveData()
    var searchNewsPage : Int = 1


    init {
        getBreakingNews("us")
    }

    fun handleToggleMenu() {
        updateState {
            it.copy(isShowMenu = !it.isShowMenu)
        }
    }

    fun handleNightMode() {
        updateState {
            it.copy(isDarkMode = !it.isDarkMode)
        }
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        safeBreakingNewsCall(countryCode)
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        safeSearchNewsCall(searchQuery)
    }

    private fun handleBreakingNewsResponses(response: Response<NewsResponseDto>): Resource<NewsResponseDto> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                breakingNewsPage++
                if (breakingNewsResponse == null) {
                    breakingNewsResponse = resultResponse
                } else {
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponses(response: Response<NewsResponseDto>, searchQuery: String): Resource<NewsResponseDto> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchNewsPage++
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveArticle(article: ArticleDto) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getSaveNews() = newsRepository.getSavedNews()

    suspend fun checkSaveArticle(url: String) : Boolean = newsRepository.checkSavedArticle(url) > 0

    fun deleteArticle(article: ArticleDto) = viewModelScope.launch {
            newsRepository.deleteArticle(article)
        }

        private suspend fun safeSearchNewsCall(searchQuery: String) {
            searchNews.postValue(Resource.Loading())
            try {
                //если есть интернет соединение, то делаем запрос к API
                if (hasInternetConnection()) {
                    val response = newsRepository.searchNews(searchQuery, searchNewsPage)
                    searchNews.postValue(handleSearchNewsResponses(response, searchQuery))
                } else {
                    searchNews.postValue(Resource.Error("No internet connection"))
                }

            } catch (t: Throwable) {
                //в теории на этап этапе могут появится только 2 ошибки
                when (t) {
                    //ошибка подключения к интернету
                    is IOException -> searchNews.postValue(Resource.Error("Network Failure"))
                    //ошибка преобразования json -> kotlin object
                    else -> searchNews.postValue(Resource.Error("Conversion Error"))
                }
            }
        }

        private suspend fun safeBreakingNewsCall(countryCode: String) {
            breakingNews.postValue(Resource.Loading())
            try {
                //если есть интернет соединение, то делаем запрос к API
                if (hasInternetConnection()) {
                    val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
                    breakingNews.postValue(handleBreakingNewsResponses(response))
                } else {
                    breakingNews.postValue(Resource.Error("No internet connection"))
                }

            } catch (t: Throwable) {
                //в теории на этап этапе могут появится только 2 ошибки
                when (t) {
                    //ошибка подключения к интернету
                    is IOException -> breakingNews.postValue(Resource.Error("Network Failure"))
                    //ошибка преобразования json -> kotlin object
                    else -> breakingNews.postValue(Resource.Error("Conversion Error"))
                }
            }
        }

        @SuppressLint("ServiceCast")
        private fun hasInternetConnection(): Boolean {
            val connectivityManager = getApplication<NewsApplication>().getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager
            //условие нужно для корректного определения подключения к интернету
            // для sdk < 23 выполняется блок else
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val activeNetwork = connectivityManager.activeNetwork ?: return false
                //позволяет проверить подключение к разным сетям,перебор вариантов происходит в when
                val capabilities =
                    connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
                return when {
                    capabilities.hasTransport(TRANSPORT_WIFI) -> true
                    capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                    capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } else {
                connectivityManager.activeNetworkInfo?.run {
                    return when (type) {
                        TYPE_WIFI -> true
                        TYPE_MOBILE -> true
                        TYPE_ETHERNET -> true
                        else -> false
                    }
                }
            }
            return false
        }
    }


data class ArticleState(
    val isDarkMode: Boolean = false,
    val isShowMenu: Boolean = false
)