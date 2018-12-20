package ru.medbox.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.medbox.databinding.FragmentSettingsBinding
import ru.medbox.ui.handler.SettingsHandler

class SettingsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentSettingsBinding.inflate(inflater, container, false)
        binding.handler = SettingsHandler(this.context)
        return binding.root
    }


    companion object {
        @JvmStatic
        fun newInstance() = SettingsFragment()
    }

}
