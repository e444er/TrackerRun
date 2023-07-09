package com.moon.trackerrun.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import com.moon.trackerrun.R
import com.moon.trackerrun.databinding.RunFragmentBinding
import com.moon.trackerrun.databinding.StatisticsFragmentBinding
import com.moon.trackerrun.databinding.TrackingFragmentBinding
import com.moon.trackerrun.db.Run
import com.moon.trackerrun.services.Polylines
import com.moon.trackerrun.services.TrackService
import com.moon.trackerrun.ui.MainViewModel
import com.moon.trackerrun.utils.Constants
import com.moon.trackerrun.utils.Constants.Companion.ACTION_PAUSE_SERVICE
import com.moon.trackerrun.utils.Constants.Companion.ACTION_START_OR_RESUME_SERVICE
import com.moon.trackerrun.utils.Constants.Companion.ACTION_STOP_SERVICE
import com.moon.trackerrun.utils.Constants.Companion.MAP_VIEW_BUNDLE_KEY
import com.moon.trackerrun.utils.Constants.Companion.MAP_ZOOM
import com.moon.trackerrun.utils.Constants.Companion.POLYLINE_COLOR
import com.moon.trackerrun.utils.Constants.Companion.POLYLINE_WIDTH
import com.moon.trackerrun.utils.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Math.round
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class TrackingFragment: Fragment(R.layout.tracking_fragment) {

    private val viewModel: MainViewModel by viewModels()

    @set:Inject
    var weight: Float = 80f

    private var isTracking = false
    private var curTimeInMillis = 0L
    private var pathPoints = mutableListOf<MutableList<LatLng>>()

    private val binding by viewBinding(TrackingFragmentBinding::bind)

    private var map:GoogleMap? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mapView.getMapAsync {
            map = it
            addAllPolylines()
        }
        binding.btnToggleRun.setOnClickListener {
            toggleRun()
        }
        val mapViewBundle = savedInstanceState?.getBundle(MAP_VIEW_BUNDLE_KEY)
        binding.mapView.onCreate(mapViewBundle)

        // restore dialog instance
        if(savedInstanceState != null) {
            val cancelRunDialog = parentFragmentManager.findFragmentByTag("s") as CancelRunDialog?
            cancelRunDialog?.setYesListener {
                stopRun()
            }
        }

        binding.btnFinishRun.setOnClickListener {
            zoomToWholeTrack()
            endRunAndSaveToDB()
        }
    }

    /**
     * Subscribes to changes of LiveData objects
     */
    private fun subscribeToObservers() {
        TrackService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })

        TrackService.pathPoints.observe(viewLifecycleOwner, Observer {
            pathPoints = it
            addLatestPolyline()
            moveCameraToUser()
        })

        TrackService.timeRunInMillis.observe(viewLifecycleOwner, Observer {
            curTimeInMillis = it
            val formattedTime = TrackingUtility.getFormattedStopWatchTime(it, true)
            binding.tvTimer.text = formattedTime
        })
    }

    /**
     * Will move the camera to the user's location.
     */
    private fun moveCameraToUser() {
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

    /**
     * Adds all polylines to the pathPoints list to display them after screen rotations
     */
    private fun addAllPolylines() {
        for (polyline in pathPoints) {
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    /**
     * Draws a polyline between the two latest points.
     */
    private fun addLatestPolyline() {
        // only add polyline if we have at least two elements in the last polyline
        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2]
            val lastLatLng = pathPoints.last().last()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)

            map?.addPolyline(polylineOptions)
        }
    }

    /**
     * Updates the tracking variable and the UI accordingly
     */
    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        if (!isTracking && curTimeInMillis > 0L) {
            binding.btnToggleRun.text = getString(R.string.start_text)
            binding.btnFinishRun.visibility = View.VISIBLE
        } else if (isTracking) {
            binding.btnToggleRun.text = getString(R.string.stop_text)
            binding.btnFinishRun.visibility = View.GONE
        }
    }

    /**
     * Toggles the tracking state
     */
    @SuppressLint("MissingPermission")
    private fun toggleRun() {
        if (isTracking) {
            pauseTrackingService()
        } else {
            startOrResumeTrackingService()
        }
    }

    /**
     * Starts the tracking service or resumes it if it is currently paused.
     */
    private fun startOrResumeTrackingService() =
        Intent(requireContext(), TrackService::class.java).also {
            it.action = ACTION_START_OR_RESUME_SERVICE
            requireContext().startService(it)
        }

    /**
     * Pauses the tracking service
     */
    private fun pauseTrackingService() =
        Intent(requireContext(), TrackService::class.java).also {
            it.action = ACTION_PAUSE_SERVICE
            requireContext().startService(it)
        }

    /**
     * Stops the tracking service.
     */
    private fun stopTrackingService() =
        Intent(requireContext(), TrackService::class.java).also {
            it.action = ACTION_STOP_SERVICE
            requireContext().startService(it)
        }

    override fun onSaveInstanceState(outState: Bundle) {
        val mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY)
        if (mapViewBundle != null) {
            binding.mapView?.onSaveInstanceState(mapViewBundle)
        }
    }

    /**
     * Zooms out until the whole track is visible. Used to make a screenshot of the
     * MapView to save it in the database
     */
    private fun zoomToWholeTrack() {
        val bounds = LatLngBounds.Builder()
        for (polyline in pathPoints) {
            for (point in polyline) {
                bounds.include(point)
            }
        }
        val width = binding.mapView.width
        val height = binding.mapView.height
        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                width,
                height,
                (height * 0.05f).toInt()
            )
        )
    }

    /**
     * Saves the recent run in the Room database and ends it
     */
    private fun endRunAndSaveToDB() {
        map?.snapshot { bmp ->
            var distanceInMeters = 0
            for (polyline in pathPoints) {
                distanceInMeters += TrackingUtility.calculatePolylineLength(polyline).toInt()
            }
            val avgSpeed =
                round((distanceInMeters / 1000f) / (curTimeInMillis / 1000f / 60 / 60) * 10) / 10f
            val timestamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceInMeters / 1000f) * weight).toInt()
            val run =
                Run(bmp, timestamp, avgSpeed, distanceInMeters, curTimeInMillis, caloriesBurned)
            viewModel.insertRun(run)
            Snackbar.make(
                requireActivity().findViewById(R.id.rootView),
                "Run saved successfully.",
                Snackbar.LENGTH_LONG
            ).show()
            stopRun()
        }
    }

    /**
     * Finishes the tracking.
     */
    private fun stopRun() {
        binding.tvTimer.text = "00:00:00:00"
        stopTrackingService()
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }





    /**
     * Shows a dialog to cancel the current run.
     */


    override fun onResume() {
        binding.mapView.onResume()
        super.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }
}