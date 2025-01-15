package com.zooro.mvvmnewsapp.domain.repository

interface NetworkHelperRepository {
    fun isNetworkConnected(): Boolean
}