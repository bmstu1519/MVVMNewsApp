package com.zooro.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.google.android.material.snackbar.Snackbar
import com.zooro.mvvmnewsapp.R
import com.zooro.mvvmnewsapp.ui.NewsActivity
import com.zooro.mvvmnewsapp.viewmodels.NewsViewModel
import kotlinx.android.synthetic.main.fragment_article.*
import kotlinx.coroutines.runBlocking

class ArticleNewsFragment : Fragment(R.layout.fragment_article) {

    lateinit var viewModel: NewsViewModel
    private val args: ArticleNewsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        webViewDarkMode(viewModel)

        val article = args.article
        webView.apply {
            webViewClient = WebViewClient()
            article.url?.let { loadUrl(it) }
            setBackgroundColor(resources.getColor(R.color.color_primary))
        }

        fab.setOnClickListener {
            runBlocking {
                val isArticleSaved = viewModel.checkSaveArticle(article.url ?:"url_is_null")

                if (isArticleSaved){
                    Snackbar.make(view, "You are already save this article", Snackbar.LENGTH_SHORT).show()
                } else {
                    viewModel.saveArticle(article)
                    Snackbar.make(view, "Article saved successfully", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun webViewDarkMode(viewModel: NewsViewModel) {
        if (viewModel.state.value?.isDarkMode == true && WebViewFeature.isFeatureSupported(
                WebViewFeature.FORCE_DARK
            )
        ) {
            WebSettingsCompat.setForceDark(webView.settings, WebSettingsCompat.FORCE_DARK_ON)
        }
    }
}
