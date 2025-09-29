package ru.medbox.ui.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import ru.medbox.R
import ru.medbox.databinding.ActivityDoctorsBinding
import ru.medbox.di.ViewModelFactory
import ru.medbox.ui.BaseActivity
import ru.medbox.ui.viewmodel.DoctorListViewModel
import ru.medbox.utils.SPECIALIZATION_KEY
import ru.medbox.utils.SPECIALIZATION_NAME

class DoctorsActivity : BaseActivity() {

    private lateinit var binding: ActivityDoctorsBinding
    private lateinit var viewModel: DoctorListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val specId = intent.extras?.getInt(SPECIALIZATION_KEY)
        val specName = intent.extras?.getString(SPECIALIZATION_NAME)
        val params: HashMap<String, Any?> = hashMapOf(SPECIALIZATION_KEY to specId)

        viewModel = ViewModelProviders.of(this, ViewModelFactory(params)).get(DoctorListViewModel::class.java)
        viewModel.errorMessage.observe(this, Observer {
            if (it != null) showError(binding.root, viewModel.errorClickListener, it) else hideError()
        })

        binding = DataBindingUtil.setContentView(this, R.layout.activity_doctors)
        binding.list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.viewModel = viewModel
    }
}

