package de.mario.camera.message

import android.content.Context
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import de.mario.camera.glue.CameraControlable
import de.mario.camera.glue.MessageSendable

class BroadcastingReceiverRegister(private val control: CameraControlable) {

    private val broadcastReceiver = BroadcastingReceiver(control)

    fun registerBroadcastReceiver(context: Context) {
        val action = control.getString(MessageSendable.MessageType.IMAGE_SAVED)
        val filter = IntentFilter(action)
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, filter)
    }

    fun unregisterBroadcastReceiver(context: Context) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver)
    }
}