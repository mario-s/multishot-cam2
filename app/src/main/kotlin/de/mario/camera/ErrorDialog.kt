package de.mario.camera

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle

import android.R
import android.app.Activity

/**
 */
class ErrorDialog : DialogFragment() {

    companion object Factory {
        val ARG_MESSAGE = "message"

        fun newInstance(message: String): ErrorDialog {
            val dialog = ErrorDialog()
            val args = Bundle()
            args.putString(ARG_MESSAGE, message)
            dialog.arguments = args
            return dialog
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return create(activity, arguments.getString(ARG_MESSAGE))
    }

    private fun create(activity: Activity, msg: String): Dialog {
        return AlertDialog.Builder(activity)
                .setMessage(msg)
                .setPositiveButton(R.string.ok, { _, _ -> activity.finish() })
                .create()
    }

}