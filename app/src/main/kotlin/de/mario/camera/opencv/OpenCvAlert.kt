package de.mario.camera.opencv

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import de.mario.camera.R

/**
 * Shows a dialog that OpenCv Package Manager is required.
 */
object OpenCvAlert {

    fun show(context: Context) {
        AlertDialog.Builder(context)
                .setMessage(R.string.info_message)
                .setPositiveButton(android.R.string.yes, object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        OpenCvLoader.init(context)
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show()
    }
}