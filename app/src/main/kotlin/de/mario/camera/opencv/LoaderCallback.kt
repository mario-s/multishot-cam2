package de.mario.camera.opencv

import android.content.Context
import android.content.Intent

import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface

/**
 * Callback when OpenCV is loaded.
 */
class LoaderCallback internal constructor(context: Context, private val intent: Intent) : BaseLoaderCallback(context) {

    override fun onManagerConnected(status: Int) {
        if (status == LoaderCallbackInterface.SUCCESS) {
            mAppContext.startService(intent)
        }
    }
}