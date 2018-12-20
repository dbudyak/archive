package ru.medbox.ui.fragment

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.medbox.databinding.FragmentMainBinding
import ru.medbox.di.ViewModelFactory
import ru.medbox.ui.handler.MainEventHandler
import ru.medbox.ui.viewmodel.SpecializationListViewModel

class MainFragment : Fragment() {

    private lateinit var viewModel: SpecializationListViewModel
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProviders.of(this, ViewModelFactory()).get(SpecializationListViewModel::class.java)

        binding = FragmentMainBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.handler = MainEventHandler(activity)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = ru.medbox.ui.fragment.MainFragment()
    }
}
