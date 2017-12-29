package de.mario.camera


import android.os.Handler
import android.os.Looper
import android.os.Message
import de.mario.camera.glue.MessageType


/**
 * This class handles incoming messages from the sub parts.
 */
class MessageHandler(private val control: CameraFragment) : Handler(Looper.getMainLooper()) {

    override fun handleMessage(message: Message) {
        when(message.what) {
            MessageType.IMAGE_SAVED -> {
                val filename = message.data.getString(MessageType.FILE)
                control.appendSavedFile(filename)
            }
            else -> control.showToast(message.obj.toString())
        }

    }

    companion object {
        private val TAG = MessageHandler::class.java.simpleName

        private val PHOTOS_SAVED = R.string.photos_saved
    }
}