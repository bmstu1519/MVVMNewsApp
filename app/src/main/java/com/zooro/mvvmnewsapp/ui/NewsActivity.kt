package com.zooro.mvvmnewsapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.zooro.mvvmnewsapp.R
import com.zooro.mvvmnewsapp.db.ArticleDatabase
import com.zooro.mvvmnewsapp.repository.NewsRepository
import com.zooro.mvvmnewsapp.viewmodels.ArticleState
import com.zooro.mvvmnewsapp.viewmodels.NewsViewModel
import com.zooro.mvvmnewsapp.viewmodels.NewsViewModelProviderFactory
import kotlinx.android.synthetic.main.activity_news.*
import kotlinx.android.synthetic.main.layout_submenu.*

class NewsActivity : AppCompatActivity() {

    lateinit var viewModel: NewsViewModel
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        setupSubmenu()

        window.statusBarColor = ContextCompat.getColor(this,R.color.color_primary)

        val newsRepository = NewsRepository(ArticleDatabase.invoke(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(application,newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.newsHavHostFragment) as NavHostFragment
        navController = navHostFragment.findNavController()

        bottomNavigationView.setupWithNavController(navController)
        setupToolbar(navController)

        viewModel.state

        viewModel.observeState(this){
            renderUi(it)
        }
    }

    private fun setupSubmenu(){
        switch_mode.setOnClickListener { viewModel.handleNightMode() }
        btn_settings.setOnClickListener { viewModel.handleToggleMenu() }

    }

    private fun setupToolbar(navController: NavController){
        setSupportActionBar(toolbar)
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.breakingNewsFragment,
            R.id.searchNewsFragment,
            R.id.savedNewsFragment
        ))
        toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    private fun renderUi(data: ArticleState) {
        btn_settings.isChecked = data.isShowMenu
        if (data.isShowMenu) submenu.open() else submenu.close()

        switch_mode.isChecked = data.isDarkMode
        delegate.localNightMode =
            if (data.isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO

    }


}