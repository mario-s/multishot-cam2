package de.mario.camera.process

import android.content.Context
import android.content.Intent

import org.opencv.android.OpenCVLoader

import de.mario.camera.glue.FusionProcessControlable

/**
 * Controller to start the HDR process.
 */
class FusionProcessController(private val context: Context) : FusionProcessControlable {

    override fun process(pictures: Array<String>) {
        val intent = Intent(context, ExposureMergeService::class.java)
        intent.putExtra(ExposureMergeService.PARAM_PICS, pictures)
        val callback = OpenCvLoaderCallback(context, intent)
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, context, callback)
    }

}