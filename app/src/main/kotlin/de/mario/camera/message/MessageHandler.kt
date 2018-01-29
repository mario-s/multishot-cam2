package de.mario.camera.message


import android.os.Handler
import android.os.Looper
import android.os.Message
import de.mario.camera.CameraFragment
import de.mario.camera.glue.MessageSendable


/**
 * This class handles incoming messages from the sub parts.
 */
class MessageHandler(private val control: CameraFragment) : Handler(Looper.getMainLooper()) {

    override fun handleMessage(message: Message) {
        when(message.what) {
            MessageSendable.MessageType.IMAGE_SAVED -> {
                val filename = message.data.getString(MessageSendable.MessageType.FILE)
                control.appendSavedFile(filename)
            }
            else -> control.showToast(message.obj.toString())
        }

    }
}