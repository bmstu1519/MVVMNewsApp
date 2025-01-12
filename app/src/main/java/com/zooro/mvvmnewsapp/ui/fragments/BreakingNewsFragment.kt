package com.zooro.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zooro.mvvmnewsapp.R
import com.zooro.mvvmnewsapp.databinding.FragmentBreakingNewsBinding
import com.zooro.mvvmnewsapp.domain.model.PaginationState
import com.zooro.mvvmnewsapp.ui.NewsActivity
import com.zooro.mvvmnewsapp.ui.adapters.NewsAdapter
import com.zooro.mvvmnewsapp.ui.viewmodels.NewsViewModelV2
import kotlinx.coroutines.launch

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {
    private lateinit var viewModel: NewsViewModelV2
    private lateinit var newsAdapter: NewsAdapter
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
        viewModel = (activity as NewsActivity).viewModel
        setupRecyclerView()
        setupPagination()
        setupClickListeners()
        observeUiState()

        viewModel.getBreakingNews("us", isFirstLoad = true)
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

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { uiState ->
                    handlePagination(uiState.paginationState)
                    handleError(uiState.errorMessage)
                }
            }
        }
    }

    private fun handlePagination(paginationState: PaginationState?) {
        paginationState?.let { state ->
            newsAdapter.differ.submitList(state.items)

            binding.paginationProgressBar.isVisible = state.isLoading

            if (state.isLastPage) {
                binding.rvBreakingNews.setPadding(0, 0, 0, 0)
            }
        }
    }

    private fun handleError(errorMessage: String?) {
        errorMessage?.let {
            Toast.makeText(activity, "An error occurred: $it", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupPagination() {
        binding.rvBreakingNews.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy > 0) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    val shouldLoadMore = visibleItemCount + firstVisibleItemPosition >= totalItemCount - 5
                    if (shouldLoadMore) {
                        viewModel.getBreakingNews("us")
                    }
                }
            }
        })
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

//    private fun setupSwipeToRefresh() {
//        binding.swipeRefreshLayout.setOnRefreshListener {
//            viewModel.getBreakingNews("us", isFirstLoad = true)
//            binding.swipeRefreshLayout.isRefreshing = false
//        }
//    }
}