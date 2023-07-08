package com.moon.trackerrun.ui.fragment

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.moon.trackerrun.R

class CancelRunDialog : DialogFragment() {

    private var yesListener: (() -> Unit)? = null

    fun setYesListener(listener: (() -> Unit)) {
        yesListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Cancel the Run")
            .setMessage("Are you sure that you want ot cancel the current run and delete its data?")
            .setIcon(R.drawable.baseline_delete_24)
            .setPositiveButton("Yes") { _, _ ->
                yesListener?.let { yes ->
                    yes()
                }
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
            .create()

    }
}