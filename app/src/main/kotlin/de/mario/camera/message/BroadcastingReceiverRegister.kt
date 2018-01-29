package de.mario.camera.message

import android.content.Context
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import de.mario.camera.R
import de.mario.camera.glue.CameraControlable

class BroadcastingReceiverRegister(private val control: CameraControlable) {

    private val broadcastReceiver = BroadcastingReceiver(control)

    fun registerBroadcastReceiver(context: Context) {
        val action = control.getString(R.string.EXPOSURE_MERGE)
        val filter = IntentFilter(action)
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, filter)
    }

    fun unregisterBroadcastReceiver(context: Context) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver)
    }
}