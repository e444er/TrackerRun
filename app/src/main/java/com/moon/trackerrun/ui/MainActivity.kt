package com.moon.trackerrun.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.moon.trackerrun.R
import com.moon.trackerrun.databinding.ActivityMainBinding
import com.moon.trackerrun.utils.Constants.Companion.ACTION_SHOW_TRACKING_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var nav: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        navigatePending(intent)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
         nav = navHostFragment.findNavController()
        binding.bottomNavigationView.setupWithNavController(navHostFragment.navController)

        nav.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.setupFragment, R.id.trackingFragment -> binding.bottomNavigationView.visibility =
                    View.GONE

                else -> binding.bottomNavigationView.visibility = View.VISIBLE
            }
        }
    }
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigatePending(intent)
    }
    private fun navigatePending(intent: Intent?) {
        if (intent?.action == ACTION_SHOW_TRACKING_FRAGMENT) {
            nav.navigate(R.id.action_global_trackingFragment)
        }
    }
}