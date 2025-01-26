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
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.zooro.mvvmnewsapp.R
import com.zooro.mvvmnewsapp.databinding.FragmentBreakingNewsBinding
import com.zooro.mvvmnewsapp.di.DependencyProvider
import com.zooro.mvvmnewsapp.ui.adapters.NewsAdapterV2
import com.zooro.mvvmnewsapp.ui.util.showSnackbar
import com.zooro.mvvmnewsapp.ui.viewmodel.BreakingNewsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

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
                launch {
                    viewModel.breakingNewsData.collectLatest { pagingData ->
                        newsAdapter.submitData(pagingData)
                    }
                }
                launch {
                    newsAdapter.loadStateFlow.collectLatest { loadStates ->

                        when(val refreshState = loadStates.refresh){
                            is LoadState.Error -> {
                                handleError(refreshState.error)
                                binding.paginationProgressBar.isVisible = false
                                binding.swipeRefresh.isRefreshing = false
                            }
                            LoadState.Loading -> {
                                binding.rvBreakingNews.isVisible = false
                                binding.paginationProgressBar.isVisible = true
                                binding.swipeRefresh.isRefreshing = false
                            }
                            is LoadState.NotLoading -> {
                                if (newsAdapter.itemCount > 0) {
                                    binding.rvBreakingNews.isVisible = true
                                    binding.swipeRefresh.isRefreshing = false
                                    binding.paginationProgressBar.isVisible = false
                                    binding.emptyList.noContent.isVisible = false
                                } else {
                                    binding.emptyList.noContent.isVisible = true
                                    binding.paginationProgressBar.isVisible = false
                                    binding.swipeRefresh.isRefreshing = false
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun handleError(throwable: Throwable) {
        val errorMessage = when (throwable) {
            is IOException -> "Ошибка сети. Проверьте подключение"
            is HttpException -> "Ошибка сервера: ${throwable.code()}"
            else -> "Произошла ошибка: ${throwable.message}"
        }

        showSnackbar(
            message = errorMessage,
            actionText = "Повторить",
            action = { newsAdapter.retry() },
        )
    }
}