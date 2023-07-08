package com.moon.trackerrun.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.moon.trackerrun.R
import com.moon.trackerrun.databinding.RunFragmentBinding
import com.moon.trackerrun.databinding.StatisticsFragmentBinding
import com.moon.trackerrun.databinding.TrackingFragmentBinding
import com.moon.trackerrun.ui.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackingFragment: Fragment(R.layout.tracking_fragment) {

    private val viewModel: MainViewModel by viewModels()

    private val binding by viewBinding(StatisticsFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
    }
}