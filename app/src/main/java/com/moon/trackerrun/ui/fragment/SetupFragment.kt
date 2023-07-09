package com.moon.trackerrun.ui.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.moon.trackerrun.R
import com.moon.trackerrun.databinding.SettingsFragmentBinding
import com.moon.trackerrun.databinding.SetupFragmentBinding
import com.moon.trackerrun.utils.Constants.Companion.KEY_FIRST_TIME_TOGGLE
import com.moon.trackerrun.utils.Constants.Companion.KEY_NAME
import com.moon.trackerrun.utils.Constants.Companion.KEY_WEIGHT
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment: Fragment(R.layout.setup_fragment) {

    private val binding by viewBinding(SetupFragmentBinding::bind)
    @Inject
    lateinit var sharedPref: SharedPreferences

    @set:Inject
    var firstTimeAppOpen: Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!firstTimeAppOpen) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.setupFragment, true)
                .build()
            findNavController().navigate(
                R.id.action_setupFragment_to_runFragment,
                savedInstanceState,
                navOptions
            )
        }

        binding.tvContinue.setOnClickListener {
            val success = writePersonalDataToSharedPref()
            if (success) {
                findNavController().navigate(R.id.action_setupFragment_to_runFragment)
            } else {
                Snackbar.make(requireView(), "Please enter all the fields.", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
    }

    /**
     * Saves the name and the weight in shared preferences
     */
    private fun writePersonalDataToSharedPref(): Boolean {
        val name = binding.etName.text.toString()
        val weightText = binding.etWeight.text.toString()
        if (name.isEmpty() || weightText.isEmpty()) {
            return false
        }
        sharedPref.edit()
            .putString(KEY_NAME, name)
            .putFloat(KEY_WEIGHT, weightText.toFloat())
            .putBoolean(KEY_FIRST_TIME_TOGGLE, false)
            .apply()
        val toolbarText = "Let's go, $name!"
        return true
    }

}