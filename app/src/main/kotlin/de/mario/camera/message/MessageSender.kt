package de.mario.camera.message

import android.os.Handler
import android.os.Message
import de.mario.camera.glue.MessageSendable

/**
 * This class send messages from any client to registered [Handler].
 */
class MessageSender(private val handler: Handler) : MessageSendable{

    override fun send(message: Message) {
        handler.sendMessage(message)
    }

    override fun send(message: String) {
        send(createMessage(message))
    }

    internal fun createMessage(message: String): Message {
        val msg = Message.obtain()
        msg.obj = message
        return msg
    }
}