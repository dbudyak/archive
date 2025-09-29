package ru.medbox.ui.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import ru.medbox.R
import ru.medbox.databinding.ItemCategoryBinding
import ru.medbox.db.model.Category
import ru.medbox.ui.handler.SalutaryEventHandler
import ru.medbox.ui.viewmodel.CategoryItemViewModel

class CategoryListAdapter : RecyclerView.Adapter<CategoryListAdapter.ViewHolder>() {
    private lateinit var categoryArticleModellList: List<Category>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemCategoryBinding>(LayoutInflater.from(parent.context), R.layout.item_category, parent, false)
        binding.handler = SalutaryEventHandler(parent.context)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(categoryArticleModellList[position])
    override fun getItemCount(): Int = if (::categoryArticleModellList.isInitialized) categoryArticleModellList.size else 0

    fun updateArticleList(categoryArticleModelList: List<Category>) {
        this.categoryArticleModellList = categoryArticleModelList
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        private val viewModel = CategoryItemViewModel()

        fun bind(categoryArticleModel: Category) {
            viewModel.bind(categoryArticleModel)
            binding.viewModel = viewModel
            binding.category = categoryArticleModel
        }
    }
}