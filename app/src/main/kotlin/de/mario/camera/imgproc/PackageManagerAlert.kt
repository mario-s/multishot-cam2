package de.mario.camera.imgproc

import android.app.AlertDialog
import android.content.Context
import de.mario.camera.R

/**
 * Shows a dialog that OpenCv Package Manager is required.
 */
object PackageManagerAlert {

    fun show(context: Context, declineListener: () -> Unit = {}) {
        AlertDialog.Builder(context)
                .setTitle(R.string.opencv_alert_title)
                .setMessage(R.string.opencv_alert_message)
                .setPositiveButton(android.R.string.yes) {_, _ -> Loader.init(context)}
                .setNegativeButton(android.R.string.no) {_,_ -> declineListener()}
                .show()
    }
}