package de.mario.camera.glue

import android.os.Handler

/**
 */
interface CameraControlable {
    fun getMessageHandler(): Handler
    fun showToast(msg: String)
    fun getString(id: Int): String
}