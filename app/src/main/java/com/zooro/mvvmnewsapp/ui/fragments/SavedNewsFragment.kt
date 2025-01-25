package com.zooro.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.zooro.mvvmnewsapp.R
import com.zooro.mvvmnewsapp.databinding.FragmentSavedNewsBinding
import com.zooro.mvvmnewsapp.di.DependencyProvider
import com.zooro.mvvmnewsapp.ui.adapters.NewsAdapter
import com.zooro.mvvmnewsapp.ui.viewmodel.SavedNewsViewModel
import kotlinx.coroutines.launch

class SavedNewsFragment : Fragment(R.layout.fragment_saved_news) {

    lateinit var viewModel: SavedNewsViewModel
    lateinit var newsAdapter: NewsAdapter
    lateinit var binding: FragmentSavedNewsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSavedNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModelFactory = DependencyProvider.viewModelFactory
        viewModel = ViewModelProvider(this, viewModelFactory)[SavedNewsViewModel::class.java]
        setupRecyclerView()
        setupClickListeners()
        observeUiState()
        setupSwipeToDelete()

    }

    private fun setupSwipeToDelete() {
        ItemTouchHelper(createItemTouchHelperCallback()).apply {
            attachToRecyclerView(binding.rvSavedNews)
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.articles.collect { articles ->
                    newsAdapter.differ.submitList(articles)
                }
            }
        }
    }


    private fun setupClickListeners() {
        newsAdapter.setOnItemClickListener { article ->
            val bundle = Bundle().apply {
                putParcelable("article", article)
            }
            findNavController().navigate(
                R.id.action_savedNewsFragment_to_articleNewsFragment,
                bundle
            )
        }
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.rvSavedNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun createItemTouchHelperCallback(): ItemTouchHelper.SimpleCallback {
        return object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val currentList = newsAdapter.differ.currentList.toMutableList()
                val article = currentList[position]

                currentList.removeAt(position)
                newsAdapter.differ.submitList(currentList)

                val snackbar = Snackbar.make(requireView(), "Статья удалена", Snackbar.LENGTH_LONG)
                snackbar.setAction("Отменить") {
                    val updatedList = newsAdapter.differ.currentList.toMutableList()
                    updatedList.add(position, article)
                    newsAdapter.differ.submitList(updatedList)
                }

                snackbar.addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        if (event != DISMISS_EVENT_ACTION) {
                            viewModel.deleteArticle(article)
                        }
                    }
                })
                snackbar.show()
            }
        }
    }
}

