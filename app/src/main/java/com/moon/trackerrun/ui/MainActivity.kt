package com.moon.trackerrun.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.moon.trackerrun.R
import com.moon.trackerrun.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val nav = navHostFragment.findNavController()
        binding.bottomNavigationView.setupWithNavController(navHostFragment.navController)

        nav.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.setupFragment, R.id.trackingFragment -> binding.bottomNavigationView.visibility =
                    View.GONE

                else -> binding.bottomNavigationView.visibility = View.VISIBLE
            }
        }

    }
}