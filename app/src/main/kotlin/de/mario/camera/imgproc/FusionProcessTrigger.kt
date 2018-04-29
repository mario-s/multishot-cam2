package de.mario.camera.imgproc

import android.content.Context
import android.content.Intent
import de.mario.camera.R
import de.mario.camera.glue.SettingsAccessable


/**
 * Trigger to start the HDR process.
 */
internal class FusionProcessTrigger(private val context: Context, private val settings: SettingsAccessable) {

    fun process(pictures: Array<String>) {
        val intent = Intent(context, FusionService::class.java)
        intent.putExtra(FusionService.PICTURES, pictures)
        intent.putExtra(FusionService.SYSTEM_NOTIFY, settings.isEnabled(R.string.notifyHdr))

        Loader.init(context, ServiceLoaderCallback(context, intent))
    }

}