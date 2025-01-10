package com.zooro.mvvmnewsapp.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.os.Build
import com.zooro.mvvmnewsapp.domain.repository.NetworkHelperRepository

class NetworkHelperRepositoryImpl(
    private val context: Context
) : NetworkHelperRepository {
    override fun isNetworkConnected(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

            capabilities.hasTransport(TRANSPORT_WIFI) ||
                    capabilities.hasTransport(TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(TRANSPORT_ETHERNET)
        } else {
            @Suppress("DEPRECATION")
            connectivityManager.activeNetworkInfo?.isConnected ?: false
        }
    }
}