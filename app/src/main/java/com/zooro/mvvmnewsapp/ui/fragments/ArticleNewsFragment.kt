package com.zooro.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.google.android.material.snackbar.Snackbar
import com.zooro.mvvmnewsapp.R
import com.zooro.mvvmnewsapp.databinding.FragmentArticleBinding
import com.zooro.mvvmnewsapp.ui.NewsActivity
import com.zooro.mvvmnewsapp.ui.viewmodels.NewsViewModel
import kotlinx.coroutines.runBlocking

class ArticleNewsFragment : Fragment(R.layout.fragment_article) {

    lateinit var viewModel: NewsViewModel
    private val args: ArticleNewsFragmentArgs by navArgs()
    private lateinit var binding: FragmentArticleBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        webViewDarkMode(viewModel)


        val article = args.article
        binding.webView.apply {
            webViewClient = WebViewClient()
            article.url?.let { loadUrl(it) }
            setBackgroundColor(resources.getColor(R.color.color_primary))
        }

        binding.fab.setOnClickListener {
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
            WebSettingsCompat.setForceDark(binding.webView.settings, WebSettingsCompat.FORCE_DARK_ON)
        }
    }
}
