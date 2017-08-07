package de.mario.camera

import android.media.Image
import android.util.Log
import de.mario.camera.glue.CameraControlable
import de.mario.camera.message.MessageSender
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 */
class ImageSaver(val control: CameraControlable, val image: Image) : Runnable {
    private val TAG = "ImageSaver"
    private val sender = MessageSender(control.getMessageHandler())

    override fun run() {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        var output: FileOutputStream? = null
        try {
            output = FileOutputStream(getFile())
            output?.write(bytes)
        } catch (e: IOException) {
            Log.w(TAG, e.message, e)
        } finally {
            image.close()
            try {
                output?.close()
                sendMessage()
            } catch (e: IOException) {
                Log.w(TAG, e.message, e)
            }
        }
    }

    private fun getFile(): File {
        return File(control.getPictureSaveLocation(), "pic.jpg")
    }

    private fun sendMessage() {
        sender.send("foo")
    }
}