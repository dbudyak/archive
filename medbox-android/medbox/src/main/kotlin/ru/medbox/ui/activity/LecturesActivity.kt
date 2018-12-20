package ru.medbox.ui.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import ru.medbox.R
import ru.medbox.databinding.ActivityLecturesBinding
import ru.medbox.di.ViewModelFactory
import ru.medbox.ui.BaseActivity
import ru.medbox.ui.viewmodel.LectureListViewModel

class LecturesActivity : BaseActivity() {
    private lateinit var binding: ActivityLecturesBinding
    private lateinit var viewModel: LectureListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, ViewModelFactory()).get(LectureListViewModel::class.java)
        viewModel.errorMessage.observe(this, Observer {
            if (it != null) showError(binding.root, viewModel.errorClickListener, it) else hideError()
        })

        binding = DataBindingUtil.setContentView(this, R.layout.activity_lectures)
        binding.list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.viewModel = viewModel
    }
}
