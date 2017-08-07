package de.mario.camera.glue

import android.os.Message

/**
 */
interface MessageSendable {
    fun send(message: Message)

    fun send(message: String)
}