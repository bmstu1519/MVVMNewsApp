package com.zooro.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.zooro.mvvmnewsapp.R
import com.zooro.mvvmnewsapp.databinding.FragmentArticleBinding
import com.zooro.mvvmnewsapp.ui.NewsActivity
import com.zooro.mvvmnewsapp.ui.viewmodels.NewsViewModelV2
import kotlinx.coroutines.launch

class ArticleNewsFragment : Fragment(R.layout.fragment_article) {

    lateinit var viewModel: NewsViewModelV2
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
//        webViewDarkMode(viewModel)
        observeUiState()

        val article = args.article
        binding.webView.apply {
            webViewClient = WebViewClient()
            article.url?.let { loadUrl(it) }
            setBackgroundColor(resources.getColor(R.color.color_primary))
        }

        binding.fab.setOnClickListener {
            viewModel.checkArticleSaved(article)
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { uiState ->
                    uiState.isArticleSaved?.let { isSaved ->
                        makeToast(isSaved)
                    }
                }
            }
        }
    }

    private fun makeToast(isSaved: Boolean) = if (isSaved) {
        Snackbar.make(requireView(), "You are already save this article", Snackbar.LENGTH_SHORT).show()
    } else {
        Snackbar.make(requireView(), "Article saved successfully", Snackbar.LENGTH_SHORT).show()
    }

//    private fun webViewDarkMode(viewModel: NewsViewModel) {
//        if (viewModel.state.value?.isDarkMode == true && WebViewFeature.isFeatureSupported(
//                WebViewFeature.FORCE_DARK
//            )
//        ) {
//            WebSettingsCompat.setForceDark(binding.webView.settings, WebSettingsCompat.FORCE_DARK_ON)
//        }
//    }
}
