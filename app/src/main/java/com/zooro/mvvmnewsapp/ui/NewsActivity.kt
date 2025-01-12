package com.zooro.mvvmnewsapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.zooro.mvvmnewsapp.R
import com.zooro.mvvmnewsapp.di.DependencyProvider
import com.zooro.mvvmnewsapp.databinding.ActivityNewsBinding
import com.zooro.mvvmnewsapp.databinding.LayoutSubmenuBinding
import com.zooro.mvvmnewsapp.ui.viewmodel.NewsViewModel

class NewsActivity : AppCompatActivity() {

    lateinit var viewModel: NewsViewModel
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var activityNewsBinding: ActivityNewsBinding
    private lateinit var submenuBinding: LayoutSubmenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelFactory = DependencyProvider.viewModelFactory
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(NewsViewModel::class.java)

        activityNewsBinding = ActivityNewsBinding.inflate(layoutInflater)
        submenuBinding = LayoutSubmenuBinding.inflate(layoutInflater)
        setContentView(activityNewsBinding.root)
        setupSubmenu()

        window.statusBarColor = ContextCompat.getColor(this,R.color.color_primary)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.newsHavHostFragment) as NavHostFragment
        navController = navHostFragment.findNavController()

        activityNewsBinding.bottomNavigationView.setupWithNavController(navController)
        setupToolbar(navController)

        viewModel.state

//        viewModel.observeState(this){
//            renderUi(it)
//        }
    }

    private fun setupSubmenu(){
        submenuBinding.switchMode.setOnClickListener { viewModel.handleNightMode() }
        activityNewsBinding.btnSettings.setOnClickListener { viewModel.handleToggleMenu() }

    }

    private fun setupToolbar(navController: NavController){
        setSupportActionBar(activityNewsBinding.toolbar)
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.breakingNewsFragment,
            R.id.searchNewsFragment,
            R.id.savedNewsFragment
        ))
        activityNewsBinding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }

//    private fun renderUi(data: ArticleState) {
//        activityNewsBinding.btnSettings.isChecked = data.isShowMenu
//        if (data.isShowMenu) activityNewsBinding.submenu.open() else activityNewsBinding.submenu.close()
//
//        submenuBinding.switchMode.isChecked = data.isDarkMode
//        delegate.localNightMode =
//            if (data.isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
//            else AppCompatDelegate.MODE_NIGHT_NO
//
//    }


}