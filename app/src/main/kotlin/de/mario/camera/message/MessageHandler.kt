package de.mario.camera.message


import android.os.Handler
import android.os.Looper
import android.os.Message
import de.mario.camera.R
import de.mario.camera.glue.CameraControllable


/**
 * This class handles incoming messages from the sub parts.
 */
class MessageHandler(val control: CameraControllable) : Handler(Looper.getMainLooper()) {

    override fun handleMessage(message: Message) {
        control.showToast(message.obj.toString())
    }

    companion object {
        private val TAG = MessageHandler::class.java.simpleName

        private val PHOTOS_SAVED = R.string.photos_saved
    }
}