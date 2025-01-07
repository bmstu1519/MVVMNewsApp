package com.zooro.mvvmnewsapp

import android.app.Application
import com.zooro.mvvmnewsapp.di.DependencyProvider

class NewsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DependencyProvider.init(this)
    }
}