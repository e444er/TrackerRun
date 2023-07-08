package com.moon.trackerrun.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.moon.trackerrun.R
import com.moon.trackerrun.databinding.SettingsFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment: Fragment(R.layout.settings_fragment) {

    private val binding by viewBinding(SettingsFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
    }

}