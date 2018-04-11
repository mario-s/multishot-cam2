package de.mario.camera.opencv

import android.app.AlertDialog
import android.content.Context
import de.mario.camera.R

/**
 * Shows a dialog that OpenCv Package Manager is required.
 */
object OpenCvAlert {

    fun show(context: Context, noListener: () -> Unit = {}) {
        AlertDialog.Builder(context)
                .setMessage(R.string.info_message)
                .setPositiveButton(android.R.string.yes) {_, _ -> OpenCvLoader.init(context)}
                .setNegativeButton(android.R.string.no) {_,_ -> noListener()}
                .show()
    }
}