package com.moon.trackerrun.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.moon.trackerrun.R
import com.moon.trackerrun.databinding.RunFragmentBinding
import com.moon.trackerrun.databinding.SettingsFragmentBinding
import com.moon.trackerrun.ui.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RunFragment: Fragment(R.layout.run_fragment) {

    private val viewModel: MainViewModel by viewModels()

    private val binding by viewBinding(RunFragmentBinding::bind)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
        }
    }
}