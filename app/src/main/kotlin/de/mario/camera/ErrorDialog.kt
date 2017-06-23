package de.mario.camera

import android.content.DialogInterface
import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle

import android.R

/**
 */
class ErrorDialog : DialogFragment() {

    companion object Factory {
        val ARG_MESSAGE = "message"

        fun newInstance(message: String): ErrorDialog {
            val dialog = ErrorDialog()
            val args = Bundle()
            args.putString(ARG_MESSAGE, message)
            dialog.setArguments(args)
            return dialog
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle): Dialog {
        return AlertDialog.Builder(activity)
                .setMessage(arguments.getString(ARG_MESSAGE))
                .setPositiveButton(R.string.ok, DialogInterface.OnClickListener { _, _ -> activity.finish() })
                .create()
    }

}