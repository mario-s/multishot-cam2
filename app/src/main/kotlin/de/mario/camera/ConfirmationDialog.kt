package de.mario.camera

import android.Manifest
import android.app.Dialog
import android.app.DialogFragment
import android.app.AlertDialog
import android.support.v13.app.FragmentCompat
import android.os.Bundle


/**
 */
class ConfirmationDialog : DialogFragment() {

    private val REQUEST_CAMERA_PERMISSION = 1

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val parent = parentFragment
        return AlertDialog.Builder(activity)
                .setMessage(R.string.request_permission)
                .setPositiveButton(android.R.string.ok, { _, _ ->
                    FragmentCompat.requestPermissions(parent,
                            arrayOf<String>(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
                })
                .setNegativeButton(android.R.string.cancel, { _, _ ->
                    parent.activity?.finish()
                })
                .create()
    }
}