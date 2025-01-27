package com.zooro.mvvmnewsapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zooro.mvvmnewsapp.R
import com.zooro.mvvmnewsapp.databinding.ItemArticlePreviewBinding
import com.zooro.mvvmnewsapp.domain.model.Article

class NewsAdapterV2 : PagingDataAdapter<Article, NewsAdapterV2.ArticleViewHolder>(ARTICLE_COMPARATOR) {

    inner class ArticleViewHolder(val binding: ItemArticlePreviewBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        private val ARTICLE_COMPARATOR = object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem.url == newItem.url
            }

            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemArticlePreviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = getItem(position)

        article?.let { articleItem ->
            with(holder.binding) {
                Glide.with(root)
                    .load(articleItem.urlToImage)
                    .into(ivArticleImage)

                if(articleItem.urlToImage == null) {
                    ivArticleImage.setImageResource(R.drawable.no_content)
                }

                tvSource.text = articleItem.author
                tvTitle.text = articleItem.title
                tvDescription.text = articleItem.description
                tvPublishedAt.text = articleItem.publishedAt

                root.setOnClickListener {
                    onItemClickListener?.let { click -> click(articleItem) }
                }
            }
        }
    }

    private var onItemClickListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }

}