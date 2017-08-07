package de.mario.camera.widget

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle

import android.R
import android.app.Activity

/**
 */
class ErrorDialog : android.app.DialogFragment() {

    companion object Factory {
        val ARG_MESSAGE = "message"

        fun newInstance(message: String): de.mario.camera.widget.ErrorDialog {
            val dialog = de.mario.camera.widget.ErrorDialog()
            val args = android.os.Bundle()
            args.putString(de.mario.camera.widget.ErrorDialog.Factory.ARG_MESSAGE, message)
            dialog.arguments = args
            return dialog
        }
    }

    override fun onCreateDialog(savedInstanceState: android.os.Bundle?): android.app.Dialog {
        return create(activity, arguments.getString(de.mario.camera.widget.ErrorDialog.Factory.ARG_MESSAGE))
    }

    private fun create(activity: android.app.Activity, msg: String): android.app.Dialog {
        return android.app.AlertDialog.Builder(activity)
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, { _, _ -> activity.finish() })
                .create()
    }

}