package de.mario.camera.widget

import android.Manifest
import android.app.Dialog
import android.app.DialogFragment
import android.app.AlertDialog
import android.support.v13.app.FragmentCompat
import android.os.Bundle


/**
 */
class ConfirmationDialog : android.app.DialogFragment() {

    private val REQUEST_CAMERA_PERMISSION = 1

    override fun onCreateDialog(savedInstanceState: android.os.Bundle?): android.app.Dialog {
        val parent = parentFragment
        return android.app.AlertDialog.Builder(activity)
                .setMessage(de.mario.camera.R.string.request_permission)
                .setPositiveButton(android.R.string.ok, { _, _ ->
                    android.support.v13.app.FragmentCompat.requestPermissions(parent,
                            arrayOf<String>(android.Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
                })
                .setNegativeButton(android.R.string.cancel, { _, _ ->
                    parent.activity?.finish()
                })
                .create()
    }
}