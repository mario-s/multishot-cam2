package de.mario.camera.imgproc

import android.content.Context
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.InstallCallbackInterface

internal class DialogLoaderCallback(context: Context): BaseLoaderCallback(context) {

    override fun onManagerConnected(status: Int) {
        if (status != 3) {
            super.onManagerConnected(status)
        }
    }

    override fun onPackageInstall(operation: Int, callback: InstallCallbackInterface?) {
        if(operation == 0) {
            callback?.install()
        }else {
            super.onPackageInstall(operation, callback)
        }
    }
}