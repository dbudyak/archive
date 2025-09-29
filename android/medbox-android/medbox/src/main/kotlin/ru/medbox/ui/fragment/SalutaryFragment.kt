package ru.medbox.ui.fragment

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.medbox.databinding.FragmentSalutaryBinding
import ru.medbox.di.ViewModelFactory
import ru.medbox.ui.handler.SalutaryEventHandler
import ru.medbox.ui.viewmodel.CategoryListViewModel

class SalutaryFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentSalutaryBinding.inflate(inflater, container, false)
        binding.viewModel = ViewModelProviders.of(this, ViewModelFactory()).get(CategoryListViewModel::class.java)
        binding.handler = SalutaryEventHandler(this.context)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = ru.medbox.ui.fragment.SalutaryFragment()
    }

}
