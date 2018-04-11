package de.mario.camera.opencv

import android.content.Context
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.OpenCVLoader

internal object OpenCvLoader {

    fun init(context: Context, callback: BaseLoaderCallback = DialogLoaderCallback(context)) {
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, context, callback)
    }

}