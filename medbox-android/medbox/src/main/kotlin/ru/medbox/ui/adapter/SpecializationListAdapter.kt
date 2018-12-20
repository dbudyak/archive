package ru.medbox.ui.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import ru.medbox.R
import ru.medbox.databinding.ItemSpecializationBinding
import ru.medbox.db.model.Specialization
import ru.medbox.ui.handler.MainEventHandler
import ru.medbox.ui.viewmodel.SpecializationItemViewModel

class SpecializationListAdapter : RecyclerView.Adapter<SpecializationListAdapter.ViewHolder>() {
    private lateinit var list: List<Specialization>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemSpecializationBinding>(LayoutInflater.from(parent.context), R.layout.item_specialization, parent, false)
        binding.handler = MainEventHandler(parent.context)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])
    override fun getItemCount(): Int = if (::list.isInitialized) list.size else 0

    fun updateList(modelList: List<Specialization>) {
        this.list = modelList
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: ItemSpecializationBinding) : RecyclerView.ViewHolder(binding.root) {
        private val viewModel = SpecializationItemViewModel()

        fun bind(model: Specialization) {
            viewModel.bind(model)
            binding.viewModel = viewModel
            binding.specialization = model
        }
    }
}