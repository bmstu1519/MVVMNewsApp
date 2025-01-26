package com.zooro.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
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
import com.zooro.mvvmnewsapp.databinding.FragmentSearchNewsBinding
import com.zooro.mvvmnewsapp.di.DependencyProvider
import com.zooro.mvvmnewsapp.ui.adapters.NewsAdapterV2
import com.zooro.mvvmnewsapp.ui.viewmodel.SearchNewsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

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
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.searchNewsData.collectLatest { pagingData ->
                        newsAdapter.submitData(pagingData)
                    }
                }
                launch {
                    newsAdapter.loadStateFlow.collectLatest { loadStates ->

                        when(val refreshState = loadStates.refresh){
                            is LoadState.Error -> {
                                handleError(refreshState.error)
                                binding.paginationProgressBar.isVisible = false
                            }
                            LoadState.Loading -> {
                                binding.rvSearchNews.isVisible = false
                                binding.paginationProgressBar.isVisible = true
                            }
                            is LoadState.NotLoading -> {
                                if (newsAdapter.itemCount > 0) {
                                    binding.rvSearchNews.isVisible = true
                                    binding.paginationProgressBar.isVisible = false
                                    binding.emptyList.noContent.isVisible = false
                                } else {
                                    binding.emptyList.noContent.isVisible = true
                                    binding.paginationProgressBar.isVisible = false
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

        Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG)
            .setAction("Повторить") {
                newsAdapter.retry()
            }
            .show()
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