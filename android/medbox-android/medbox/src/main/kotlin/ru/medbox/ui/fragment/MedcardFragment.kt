package ru.medbox.ui.fragment

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.medbox.databinding.FragmentMedcardBinding
import ru.medbox.di.ViewModelFactory
import ru.medbox.ui.handler.MedcardHandler
import ru.medbox.ui.viewmodel.DoctorListViewModel

class MedcardFragment : Fragment() {

    private lateinit var doctorsViewModel: DoctorListViewModel
    private lateinit var binding: FragmentMedcardBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        doctorsViewModel = ViewModelProviders.of(this, ViewModelFactory()).get(DoctorListViewModel::class.java)

        binding = FragmentMedcardBinding.inflate(inflater, container, false)
        binding.doctorsViewModel = doctorsViewModel
        binding.handler = MedcardHandler(activity)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = ru.medbox.ui.fragment.MedcardFragment()
    }

}
