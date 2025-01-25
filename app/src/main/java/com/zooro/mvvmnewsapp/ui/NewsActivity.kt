package com.zooro.mvvmnewsapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.zooro.mvvmnewsapp.R
import com.zooro.mvvmnewsapp.databinding.ActivityNewsBinding
import com.zooro.mvvmnewsapp.databinding.LayoutSubmenuBinding

class NewsActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var activityNewsBinding: ActivityNewsBinding
    private lateinit var submenuBinding: LayoutSubmenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityNewsBinding = ActivityNewsBinding.inflate(layoutInflater)
        submenuBinding = LayoutSubmenuBinding.inflate(layoutInflater)
        setContentView(activityNewsBinding.root)

        window.statusBarColor = ContextCompat.getColor(this,R.color.color_primary)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.newsHavHostFragment) as NavHostFragment
        navController = navHostFragment.findNavController()

        activityNewsBinding.bottomNavigationView.setupWithNavController(navController)
        setupToolbar(navController)

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
}