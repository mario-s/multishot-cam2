package de.mario.camera.message

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager

object BroadcastingSender {

    fun send(context: Context?, intent: Intent) {
        if(context != null) {
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }
    }
}