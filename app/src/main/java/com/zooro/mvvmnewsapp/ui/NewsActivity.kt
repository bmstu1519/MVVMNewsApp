package com.zooro.mvvmnewsapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
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

        val newsRepository = NewsRepository(ArticleDatabase.invoke(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(application,newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.newsHavHostFragment) as NavHostFragment
        navController = navHostFragment.findNavController()


        bottomNavigationView.setupWithNavController(navController)
        setupToolbar(navController)

//        val bottom = findViewById<SwitchMaterial>(R.id.switchBottom)
//        bottom.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked){
//                Toast.makeText(this, "CHECK",Toast.LENGTH_LONG).show()
//
//            } else {
//                Toast.makeText(this, "NOT CHECK",Toast.LENGTH_LONG).show()
//
//            }
//
//        }
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        val inflater = menuInflater
//        inflater.inflate(R.menu.toolbar_menu, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when(item.itemId){
//            R.id.settings -> {
//                Toast.makeText(this, "SETTINGS",Toast.LENGTH_LONG).show()
//                true
//            }
//            else -> {
//                Toast.makeText(this, "SETTINGS",Toast.LENGTH_LONG).show()
//                true
//            }
//        }
//    }

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