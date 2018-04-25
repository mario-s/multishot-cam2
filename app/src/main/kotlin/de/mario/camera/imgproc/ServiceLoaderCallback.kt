package de.mario.camera.imgproc

import android.content.Context
import android.content.Intent

import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface

/**
 * Callback when OpenCV is loaded.
 */
internal class ServiceLoaderCallback(context: Context, private val intent: Intent) : BaseLoaderCallback(context) {

    override fun onManagerConnected(status: Int) {
        if (status == LoaderCallbackInterface.SUCCESS) {
            mAppContext.startService(intent)
        }
    }
}