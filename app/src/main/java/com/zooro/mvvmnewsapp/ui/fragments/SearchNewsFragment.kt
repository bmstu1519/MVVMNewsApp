package com.zooro.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.zooro.mvvmnewsapp.R
import com.zooro.mvvmnewsapp.databinding.FragmentSearchNewsBinding
import com.zooro.mvvmnewsapp.di.DependencyProvider
import com.zooro.mvvmnewsapp.ui.adapters.NewsAdapterV2
import com.zooro.mvvmnewsapp.ui.viewmodel.SearchNewsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {

    private lateinit var viewModel: SearchNewsViewModel
    private lateinit var newsAdapter: NewsAdapterV2
    private lateinit var binding: FragmentSearchNewsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModelFactory = DependencyProvider.viewModelFactory
        viewModel = ViewModelProvider(this, viewModelFactory)[SearchNewsViewModel::class.java]

        setupRecyclerView()
        setupClickListeners()
        setupSearchListener()
        observeUiState()
    }

    private fun setupClickListeners() {
        newsAdapter.setOnItemClickListener { article ->
            val bundle = Bundle().apply {
                putParcelable("article", article)
            }
            findNavController().navigate(
                R.id.action_searchNewsFragment_to_articleNewsFragment,
                bundle
            )
        }
    }

    private fun setupSearchListener() {
        binding.etSearch.addTextChangedListener { editable ->
            viewModel.updateQuery(editable?.toString() ?: "")
        }
    }


    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                when {
                    state.isLoading -> {
//                        binding.progressBar.isVisible = true
//                        binding.errorLayout.isVisible = false
                    }
                    state.errorMessage != null -> {
//                        binding.progressBar.isVisible = false
//                        binding.errorLayout.isVisible = true
//                        binding.errorMessage.text = state.errorMessage
                        handleError(state.errorMessage)
                    }
                    state.data != null -> {
//                        binding.progressBar.isVisible = false
//                        binding.errorLayout.isVisible = false
                        newsAdapter.submitData(state.data)
                    }
                }
            }
        }
    }

    private fun handleError(errorMessage: String?) {
        errorMessage?.let {
            Toast.makeText(
                activity,
                errorMessage,
//                "You have requested too many results.\n" +
//                        "Developer accounts are limited to a max of 100 results.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    private fun setupRecyclerView() {
        newsAdapter = NewsAdapterV2()
        binding.rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
        binding.rvSearchNews.apply {
            layoutManager = LinearLayoutManager(activity)
        }
    }
}