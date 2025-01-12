package com.zooro.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zooro.mvvmnewsapp.R
import com.zooro.mvvmnewsapp.databinding.FragmentSearchNewsBinding
import com.zooro.mvvmnewsapp.domain.model.PaginationState
import com.zooro.mvvmnewsapp.ui.NewsActivity
import com.zooro.mvvmnewsapp.ui.adapters.NewsAdapter
import com.zooro.mvvmnewsapp.ui.viewmodel.NewsViewModel
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {

    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var searchQuery: String
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
        viewModel = (activity as NewsActivity).viewModel
        setupRecyclerView()
        setupPagination()
        setupClickListeners()
        setupSearchListener()
        observeUiState()

        viewModel.searchNews(true)
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
            viewModel.updateSearchQuery(editable?.toString() ?: "")
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { uiState ->
                    handleError(uiState.errorMessage)
                    handlePagination(uiState.paginationState)
                }
            }
        }
    }

    private fun handlePagination(paginationState: PaginationState?) {
        paginationState?.let { state ->
            newsAdapter.differ.submitList(state.items)

            binding.paginationProgressBar.isVisible = state.isLoading

            if (state.isLastPage) {
                binding.rvSearchNews.setPadding(0, 0, 0, 0)
            }
        }
    }

    private fun handleError(errorMessage: String?) {
        errorMessage?.let {
            Toast.makeText(
                activity,
                "You have requested too many results.\n" +
                        "Developer accounts are limited to a max of 100 results.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun setupPagination() {
        binding.rvSearchNews.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy > 0) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    val shouldLoadMore = visibleItemCount + firstVisibleItemPosition >= totalItemCount - 5
                    if (shouldLoadMore) {
                        viewModel.searchNews()
                    }
                }
            }
        })
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}