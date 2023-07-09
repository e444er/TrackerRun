package com.moon.trackerrun.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.moon.trackerrun.R
import com.moon.trackerrun.databinding.RunFragmentBinding
import com.moon.trackerrun.databinding.SetupFragmentBinding
import com.moon.trackerrun.databinding.StatisticsFragmentBinding
import com.moon.trackerrun.ui.CustomMarkerView
import com.moon.trackerrun.ui.MainViewModel
import com.moon.trackerrun.ui.StatisticsViewModel
import com.moon.trackerrun.utils.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Math.round

@AndroidEntryPoint
class StatisticsFragment: Fragment(R.layout.statistics_fragment) {

    private val viewModel: StatisticsViewModel by viewModels()

    private val binding by viewBinding(StatisticsFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLineChart()
        subscribeToObservers()
    }

    private fun setupLineChart() {
        binding.barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }
        binding.barChart.axisLeft.apply {
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }
        binding.barChart.axisRight.apply {
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }
        binding.barChart.apply {
            description.text = "Avg Speed Over Time"
            legend.isEnabled = false
        }
    }

    private fun subscribeToObservers() {
        viewModel.totalDistance.observe(viewLifecycleOwner, Observer {
            // in case DB is empty it will be null
            it?.let {
                val km = it / 1000f
                val totalDistance = round(km * 10) / 10f
                val totalDistanceString = "${totalDistance}km"
                binding.tvTotalDistance.text = totalDistanceString
            }
        })

        viewModel.totalTimeInMillis.observe(viewLifecycleOwner, Observer {
            it?.let {
                val totalTimeInMillis = TrackingUtility.getFormattedStopWatchTime(it)
                binding.tvTotalTime.text = totalTimeInMillis
            }
        })

        viewModel.totalAvgSpeed.observe(viewLifecycleOwner, Observer {
            it?.let {
                val roundedAvgSpeed = round(it * 10f) / 10f
                val totalAvgSpeed = "${roundedAvgSpeed}km/h"
                binding.tvAverageSpeed.text = totalAvgSpeed
            }
        })

        viewModel.totalCaloriesBurned.observe(viewLifecycleOwner, Observer {
            it?.let {
                val totalCaloriesBurned = "${it}kcal"
                binding.tvTotalCalories.text = totalCaloriesBurned
            }
        })

        viewModel.runsSortedByDate.observe(viewLifecycleOwner, Observer {
            it?.let {
                val allAvgSpeeds = it.indices.map { i -> BarEntry(i.toFloat(), it[i].avgSpeedInKMH) }

                val bardataSet = BarDataSet(allAvgSpeeds, "Avg Speed over Time")
                bardataSet.apply {
                    valueTextColor = Color.WHITE
                    color = ContextCompat.getColor(requireContext(), R.color.black)
                }
                val lineData = BarData(bardataSet)
                binding.barChart.data = lineData
                val marker = CustomMarkerView(
                    it.reversed(),
                    requireContext(),
                    R.layout.marker_view
                )
                binding.barChart.marker = marker
                binding.barChart.invalidate()
            }
        })
    }
}