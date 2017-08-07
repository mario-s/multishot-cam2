package de.mario.camera.glue

import android.os.Handler
import java.io.File

/**
 */
interface CameraControlable {
    fun getMessageHandler(): Handler
    fun showToast(msg: String)
}