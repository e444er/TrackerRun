package com.moon.trackerrun.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.moon.trackerrun.R
import com.moon.trackerrun.databinding.SettingsFragmentBinding
import com.moon.trackerrun.databinding.SetupFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetupFragment: Fragment(R.layout.setup_fragment) {

    private val binding by viewBinding(SetupFragmentBinding::bind)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvContinue.setOnClickListener {
            findNavController().navigate(R.id.action_setupFragment_to_runFragment)
        }
    }
}