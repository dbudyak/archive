package ru.medbox.ui.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import ru.medbox.R
import ru.medbox.databinding.ItemDoctorBinding
import ru.medbox.db.model.Doctor
import ru.medbox.ui.handler.MainEventHandler
import ru.medbox.ui.viewmodel.DoctorItemViewModel

class DoctorListAdapter : RecyclerView.Adapter<DoctorListAdapter.ViewHolder>() {
    private lateinit var list: List<Doctor>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemDoctorBinding>(LayoutInflater.from(parent.context), R.layout.item_doctor, parent, false)
        binding.handler = MainEventHandler(parent.context)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])
    override fun getItemCount(): Int = if (::list.isInitialized) list.size else 0

    fun updateList(modelList: List<Doctor>) {
        this.list = modelList
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: ItemDoctorBinding) : RecyclerView.ViewHolder(binding.root) {
        private val viewModel = DoctorItemViewModel()

        fun bind(model: Doctor) {
            viewModel.bind(model)
            binding.viewModel = viewModel
            binding.doctor = model
        }
    }
}