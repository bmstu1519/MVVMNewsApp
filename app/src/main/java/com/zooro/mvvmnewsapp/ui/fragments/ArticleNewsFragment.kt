package com.zooro.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.zooro.mvvmnewsapp.R
import com.zooro.mvvmnewsapp.ui.NewsActivity
import com.zooro.mvvmnewsapp.ui.NewsViewModel

class ArticleNewsFragment : Fragment(R.layout.fragment_article) {

    lateinit var viewModel : NewsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
    }
}