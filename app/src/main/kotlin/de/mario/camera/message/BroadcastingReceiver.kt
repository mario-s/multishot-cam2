package de.mario.camera.message

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.mario.camera.R
import de.mario.camera.glue.CameraControlable

class BroadcastingReceiver(private val control: CameraControlable) : BroadcastReceiver(){

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == string(R.string.EXPOSURE_MERGE)) {
            val result = intent.getStringExtra(string(R.string.MERGED))
            control.showToast(result)
        }
    }

    private fun string(id: Int): String = control.getString(id)
}