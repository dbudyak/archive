package ru.medbox.ui.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import ru.medbox.R
import ru.medbox.databinding.ActivityDoctorBinding
import ru.medbox.di.ViewModelFactory
import ru.medbox.ui.BaseActivity
import ru.medbox.ui.viewmodel.DoctorDetailViewModel
import ru.medbox.utils.DOCTOR_KEY
import ru.medbox.utils.SPECIALIZATION_KEY


class DoctorDetailActivity : BaseActivity() {

    private lateinit var binding: ActivityDoctorBinding
    private lateinit var viewModel: DoctorDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val doctorId = intent.extras?.getInt(DOCTOR_KEY)
        val specializationId = intent.extras?.getInt(SPECIALIZATION_KEY)
        val params: HashMap<String, Any?> = hashMapOf(DOCTOR_KEY to doctorId, SPECIALIZATION_KEY to specializationId)

        viewModel = ViewModelProviders.of(this, ViewModelFactory(params)).get(DoctorDetailViewModel::class.java)
        viewModel.errorMessage.observe(this, Observer { errorMsg -> errorMsg?.let { showError(binding.root, viewModel.errorClickListener, it) } })

        binding = DataBindingUtil.setContentView(this, R.layout.activity_doctor)
        binding.vm = viewModel

    }

}
