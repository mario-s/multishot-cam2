package de.mario.camera.opencv

import android.content.Context
import android.content.Intent


/**
 * Trigger to start the HDR process.
 */
internal class FusionProcessTrigger(private val context: Context) {

    fun process(pictures: Array<String>) {
        val intent = Intent(context, ExposureMergeService::class.java)
        intent.putExtra(ExposureMergeService.PARAM_PICS, pictures)
        OpenCvLoader.init(context, ServiceLoaderCallback(context, intent))
    }

}