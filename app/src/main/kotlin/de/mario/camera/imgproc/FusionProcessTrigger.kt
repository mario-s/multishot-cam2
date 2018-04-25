package de.mario.camera.imgproc

import android.content.Context
import android.content.Intent


/**
 * Trigger to start the HDR process.
 */
internal class FusionProcessTrigger(private val context: Context) {

    fun process(pictures: Array<String>) {
        val intent = Intent(context, FusionService::class.java)
        intent.putExtra(FusionService.PARAM_PICS, pictures)
        Loader.init(context, ServiceLoaderCallback(context, intent))
    }

}