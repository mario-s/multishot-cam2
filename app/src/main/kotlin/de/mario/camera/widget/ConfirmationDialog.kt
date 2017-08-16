package de.mario.camera.widget

import android.Manifest
import android.app.Dialog
import android.app.DialogFragment
import android.app.AlertDialog
import android.support.v13.app.FragmentCompat
import android.os.Bundle


/**
 */
class ConfirmationDialog(private val requestCode: Int, private val permission: String) : android.app.DialogFragment() {

    override fun onCreateDialog(savedInstanceState: android.os.Bundle?): android.app.Dialog {
        val parent = parentFragment
        return android.app.AlertDialog.Builder(activity)
                .setMessage(requestCode)
                .setPositiveButton(android.R.string.ok, { _, _ ->
                    android.support.v13.app.FragmentCompat.requestPermissions(parent,
                            arrayOf<String>(permission), requestCode)
                })
                .setNegativeButton(android.R.string.cancel, { _, _ ->
                    parent.activity?.finish()
                })
                .create()
    }
}