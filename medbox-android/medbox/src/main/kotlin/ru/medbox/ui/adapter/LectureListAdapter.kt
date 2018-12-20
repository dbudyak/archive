package ru.medbox.ui.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import ru.medbox.R
import ru.medbox.databinding.ItemLectureBinding
import ru.medbox.db.model.Lecture
import ru.medbox.ui.handler.SalutaryEventHandler
import ru.medbox.ui.viewmodel.LectureItemViewModel

class LectureListAdapter : RecyclerView.Adapter<LectureListAdapter.ViewHolder>() {
    private lateinit var lectureModellList: List<Lecture>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemLectureBinding>(LayoutInflater.from(parent.context), R.layout.item_lecture, parent, false)
        val handler = SalutaryEventHandler(parent.context)
        binding.handler = handler
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(lectureModellList[position])
    override fun getItemCount(): Int = if (::lectureModellList.isInitialized) lectureModellList.size else 0

    fun updateLectureList(lectureModelList: List<Lecture>) {
        this.lectureModellList = lectureModelList
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: ItemLectureBinding) : RecyclerView.ViewHolder(binding.root) {
        private val viewModel = LectureItemViewModel()

        fun bind(lectureModel: Lecture) {
            viewModel.bind(lectureModel)
            binding.viewModel = viewModel
            binding.lecture = lectureModel
        }
    }
}