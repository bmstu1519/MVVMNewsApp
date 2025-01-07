package com.zooro.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zooro.mvvmnewsapp.R
import com.zooro.mvvmnewsapp.ui.adapters.NewsAdapter
import com.zooro.mvvmnewsapp.databinding.FragmentBreakingNewsBinding
import com.zooro.mvvmnewsapp.ui.NewsActivity
import com.zooro.mvvmnewsapp.data.api.ApiSettings.Companion.QUERY_PAGE_SIZE
import com.zooro.mvvmnewsapp.domain.model.Resource
import com.zooro.mvvmnewsapp.ui.viewmodels.NewsViewModel

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {

    lateinit var viewModel : NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    private lateinit var binding: FragmentBreakingNewsBinding

//    val TAG = "BreakingNewsFragment"

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

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleNewsFragment,
                bundle
            )
        }

        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let{ newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.breakingNewsPage == totalPages
                        if (isLastPage){
                            binding.rvBreakingNews.setPadding(0, 0,0,0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.run {
                        Toast.makeText(activity, "An error occured", Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun hideProgressBar(){
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar(){
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            //первый видимый элемент
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            //общее количество видимых элементов
            val visibleItemCount = layoutManager.childCount
            //общее количество элементов
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage =!isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            //разбивать на страницы или нет
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling

            if (shouldPaginate){
                viewModel.getBreakingNews("us")
                isScrolling = false
            }


        }
    }
    private fun setupRecyclerView(){
        newsAdapter = NewsAdapter()
        binding.rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }
}