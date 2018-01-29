package de.mario.camera.glue

import android.os.Message

/**
 */
interface MessageSendable {
    fun send(message: Message)

    fun send(message: String)

    object MessageType {
        const val IMAGE_SAVED = 1
        const val FILE = "File"
    }
}