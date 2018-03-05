package de.mario.camera.opencv

import android.content.Context
import android.content.Intent

import org.opencv.android.OpenCVLoader


/**
 * Trigger to start the HDR process.
 */
internal class FusionProcessTrigger(private val context: Context) {

    fun process(pictures: Array<String>) {
        val intent = Intent(context, ExposureMergeService::class.java)
        intent.putExtra(ExposureMergeService.PARAM_PICS, pictures)
        val callback = LoaderCallback(context, intent)
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, context, callback)
    }

}