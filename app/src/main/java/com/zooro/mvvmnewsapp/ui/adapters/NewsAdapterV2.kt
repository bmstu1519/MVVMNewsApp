package com.zooro.mvvmnewsapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zooro.mvvmnewsapp.R
import com.zooro.mvvmnewsapp.databinding.ItemArticlePreviewBinding
import com.zooro.mvvmnewsapp.domain.model.Article

/**
 * Адаптер для отображения новостей с поддержкой пагинации через Paging 3
 * Наследуемся от PagingDataAdapter вместо RecyclerView.Adapter
 */
class NewsAdapterV2 : PagingDataAdapter<Article, NewsAdapterV2.ArticleViewHolder>(ARTICLE_COMPARATOR) {

    inner class ArticleViewHolder(val binding: ItemArticlePreviewBinding) :
        RecyclerView.ViewHolder(binding.root)

    /**
     * Компаратор для сравнения элементов списка
     * Используется Paging 3 для определения изменений в списке
     */
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
        // Используем getItem(position) вместо differ.currentList[position]
        // так как теперь данными управляет PagingDataAdapter
        val article = getItem(position)

        // Проверяем, что элемент не null (важно для пагинации)
        article?.let { articleItem ->
            with(holder.binding) {
                // Загружаем изображение с помощью Glide
                Glide.with(root)
                    .load(articleItem.urlToImage)
                    .into(ivArticleImage)

                // Устанавливаем изображение по умолчанию, если URL пустой
                if(articleItem.urlToImage == null) {
                    ivArticleImage.setImageResource(R.drawable.no_content)
                }

                // Заполняем текстовые поля
                tvSource.text = articleItem.newsSource?.name
                tvTitle.text = articleItem.title
                tvDescription.text = articleItem.description
                tvPublishedAt.text = articleItem.publishedAt

                // Обработка клика по элементу
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

    // Удален метод getItemCount(), так как он уже реализован в PagingDataAdapter
}