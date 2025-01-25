package com.zooro.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.zooro.mvvmnewsapp.R
import com.zooro.mvvmnewsapp.databinding.FragmentBreakingNewsBinding
import com.zooro.mvvmnewsapp.di.DependencyProvider
import com.zooro.mvvmnewsapp.ui.adapters.NewsAdapterV2
import com.zooro.mvvmnewsapp.ui.viewmodel.BreakingNewsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {
    private lateinit var viewModel: BreakingNewsViewModel
    private lateinit var newsAdapter: NewsAdapterV2
    private lateinit var binding: FragmentBreakingNewsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBreakingNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModelFactory = DependencyProvider.viewModelFactory
        viewModel = ViewModelProvider(this, viewModelFactory)[BreakingNewsViewModel::class.java]

        setupRecyclerView()
        setupClickListeners()
        setupSwipeRefresh()
        observeUiState()
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapterV2()
        binding.rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun setupClickListeners() {
        newsAdapter.setOnItemClickListener { article ->
            val bundle = Bundle().apply {
                putParcelable("article", article)
            }
            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleNewsFragment,
                bundle
            )
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.apply {
            setOnRefreshListener {
                viewModel.refresh()
            }
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { state ->
                    binding.swipeRefresh.isRefreshing = state.isLoading

                    binding.rvBreakingNews.isVisible = !state.isLoading && state.data != null

                    binding.paginationProgressBar.isVisible = state.isLoading
                    when {
                        state.isLoading -> { }
                        state.errorMessage != null -> {
                            handleError(state.errorMessage)
                        }
                        state.data != null -> {
                            newsAdapter.submitData(state.data)
                        }
                    }
                }
            }
        }
    }

    private fun handleError(errorMessage: String?) {
        errorMessage?.let {
            Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show()
        }
    }
}