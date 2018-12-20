package ru.medbox.ui.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import ru.medbox.R
import ru.medbox.databinding.ItemArticleBinding
import ru.medbox.db.model.Article
import ru.medbox.ui.handler.SalutaryEventHandler
import ru.medbox.ui.viewmodel.ArticleItemViewModel

class ArticleListAdapter : RecyclerView.Adapter<ArticleListAdapter.ViewHolder>() {
    private lateinit var articleModellList: List<Article>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemArticleBinding>(LayoutInflater.from(parent.context), R.layout.item_article, parent, false)
        val handler = SalutaryEventHandler(parent.context)
        binding.handler = handler
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(articleModellList[position])
    override fun getItemCount(): Int = if (::articleModellList.isInitialized) articleModellList.size else 0

    fun updateArticleList(articleModelList: List<Article>) {
        this.articleModellList = articleModelList
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root) {
        private val viewModel = ArticleItemViewModel()

        fun bind(articleModel: Article) {
            viewModel.bind(articleModel)
            binding.viewModel = viewModel
            binding.article = articleModel
        }
    }
}