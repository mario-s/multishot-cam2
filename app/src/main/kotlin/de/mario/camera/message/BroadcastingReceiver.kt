package de.mario.camera.message

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.mario.camera.R
import de.mario.camera.glue.CameraControlable
import java.io.File
import java.lang.String.format

class BroadcastingReceiver(private val control: CameraControlable) : BroadcastReceiver(){

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == string(R.string.EXPOSURE_MERGE)) {
            val result = intent.getStringExtra(string(R.string.MERGED))
            val path = File(result).parent
            val msg = string(R.string.fusion_saved)
            control.showToast(format(msg, path))
        }
    }

    private fun string(id: Int): String = control.getContext().getString(id)
}