package de.mario.camera.glue

import android.os.Handler

/**
 */
interface CameraControllable {
    fun getMessageHandler(): Handler

    fun showToast(msg: String)

    fun getString(id: Int): String

    fun openCamera(width: Int, height: Int)

    fun updateTransform(viewWidth: Int, viewHeight: Int)
}